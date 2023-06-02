package io.github.swsk33.sqlinitializespringbootstarter.autoconfigure;

import io.github.swsk33.sqlinitializespringbootstarter.param.DatabasePlatformName;
import io.github.swsk33.sqlinitializespringbootstarter.properties.DatabaseInitializeProperties;
import io.github.swsk33.sqlinitializespringbootstarter.util.SQLFileUtils;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

/**
 * 数据库初始化的自动配置类
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(DatabaseInitializeProperties.class)
public class DatabaseInitializeAutoConfigure {

	/**
	 * 配置数据源信息
	 */
	@Autowired
	private OriginDatasourceAutoConfigure originDatasource;

	/**
	 * 数据库创建策略上下文
	 */
	@Autowired
	private DatabaseCreateStrategyAutoConfigure databaseCreateStrategy;

	/**
	 * 获取读取的配置文件值
	 */
	@Autowired
	private DatabaseInitializeProperties initializeProperties;

	/**
	 * 存放不同数据库平台对应的“找不到数据库”错误码，不同数据库驱动连接数据库时找不到数据库的错误码不同，例如MySQL是1049
	 * 键表示数据库平台，例如mysql
	 * 值表示这个平台的驱动抛出“找不到数据库”异常时的错误码
	 */
	private static final Map<String, Integer> DATABASE_NOT_EXIST_ERROR_CODE = new HashMap<>();

	/**
	 * 初始化所有的数据库平台的错误码
	 */
	private void initErrorCode() {
		DATABASE_NOT_EXIST_ERROR_CODE.put(DatabasePlatformName.MYSQL, 1049);
		DATABASE_NOT_EXIST_ERROR_CODE.put(DatabasePlatformName.POSTGRE_SQL, 0);
		log.info("错误码列表初始化完成！");
	}

	/**
	 * 检测当前连接的库是否存在（连接URL中的数据库）
	 *
	 * @return 当前连接的库是否存在
	 */
	private boolean currentDatabaseExists() {
		// 尝试以配置文件中的URL建立连接
		try {
			Connection connection = DriverManager.getConnection(originDatasource.getUrl(), originDatasource.getUsername(), originDatasource.getPassword());
			connection.close();
		} catch (SQLException e) {
			// 若连接抛出异常且错误代码为对应的数据库平台的“找不到数据库”异常代码，则说明数据库不存在
			if (e.getErrorCode() == DATABASE_NOT_EXIST_ERROR_CODE.get(originDatasource.getDatabasePlatform())) {
				return false;
			}
		}
		// 正常情况下说明连接URL中数据库存在
		// 或者为其它错误代码时，不能判断数据库是否存在
		return true;
	}

	/**
	 * 创建数据库
	 */
	private void createDatabase() {
		try {
			// 重新建立连接，不连接至指定数据库
			String newURL = "jdbc:" + originDatasource.getDatabasePlatform() + "://" + originDatasource.getHostAndPort() + "/";
			Connection connection = DriverManager.getConnection(newURL, originDatasource.getUsername(), originDatasource.getPassword());
			Statement statement = connection.createStatement();
			// 根据不同的数据库平台生成SQL语句
			String createSQL = databaseCreateStrategy.generateSQL(originDatasource.getDatabasePlatform(), originDatasource.getDatabaseName());
			// 若为null说明数据库平台不在支持范围，终止
			if (createSQL == null) {
				return;
			}
			// 执行SQL完成数据库创建
			statement.execute(createSQL);
			// 关闭会话与连接
			statement.close();
			connection.close();
			log.info("数据库创建完成！");
		} catch (SQLException e) {
			log.error("连接失败！");
			throw new RuntimeException(e);
		}
	}

	/**
	 * 用于检查和初始化整个数据库的方法
	 */
	@PostConstruct
	private void initDatabase() {
		initErrorCode();
		log.info("开始检查数据库是否需要初始化...");
		// 检测当前连接数据库是否存在
		if (currentDatabaseExists()) {
			log.info("数据库存在，不需要初始化！");
			return;
		}
		log.warn("数据库不存在！准备执行初始化步骤...");
		// 先创建数据库
		createDatabase();
		// 然后再次连接，执行脚本初始化库中的表格
		try (Connection connection = DriverManager.getConnection(originDatasource.getUrl(), originDatasource.getUsername(), originDatasource.getPassword())) {
			SQLFileUtils.batchRunSQLScript(initializeProperties.getSqlPaths(), connection);
			log.info("初始化表格完成！");
		} catch (Exception e) {
			log.error("初始化表格时，连接数据库失败！");
			e.printStackTrace();
		}
	}

}