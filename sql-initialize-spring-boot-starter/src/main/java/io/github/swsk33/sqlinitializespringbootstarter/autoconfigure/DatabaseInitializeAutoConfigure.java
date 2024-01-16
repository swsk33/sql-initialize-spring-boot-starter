package io.github.swsk33.sqlinitializespringbootstarter.autoconfigure;

import io.github.swsk33.sqlinitializecore.model.ConnectionMetadata;
import io.github.swsk33.sqlinitializecore.util.DatabaseMetadataUtils;
import io.github.swsk33.sqlinitializecore.util.SQLExecuteUtils;
import io.github.swsk33.sqlinitializespringbootstarter.properties.DatabaseInitializeProperties;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * 数据库初始化的自动配置类
 */
@Slf4j
@Configuration("SQLInitializeAutoConfigure")
@EnableConfigurationProperties(DatabaseInitializeProperties.class)
public class DatabaseInitializeAutoConfigure {

	/**
	 * 配置数据源信息
	 */
	@Autowired
	private OriginDatasourceAutoConfigure originDatasource;

	/**
	 * 获取读取的配置文件值
	 */
	@Autowired
	private DatabaseInitializeProperties initializeProperties;

	/**
	 * 用于检查和初始化整个数据库的方法
	 */
	@PostConstruct
	private void initDatabase() {
		log.info("开始检查数据库是否需要初始化...");
		// 组装地址并重新连接
		ConnectionMetadata urlMeta = originDatasource.getMetadata();
		String checkUrl = "jdbc:" + urlMeta.getDatabasePlatform() + "://" + urlMeta.getHostAndPort() + "/";
		try (Connection connection = DriverManager.getConnection(checkUrl, originDatasource.getUsername(), originDatasource.getPassword())) {
			// 检测数据库是否存在
			if (!DatabaseMetadataUtils.databaseExists(urlMeta.getDatabaseName(), connection)) {
				log.warn("数据库不存在！准备创建！");
				SQLExecuteUtils.createDatabase(urlMeta.getDatabasePlatform(), urlMeta.getDatabaseName(), connection);
			} else {
				log.info("数据库存在，不需要创建！");
			}
		} catch (Exception e) {
			log.error("连接至数据库检查元数据时失败！");
			log.error(e.getMessage());
		}
		// 然后再次连接，执行脚本初始化库中的表格
		try (Connection connection = DriverManager.getConnection(originDatasource.getUrl(), originDatasource.getUsername(), originDatasource.getPassword())) {
			// 检测数据库中表的数量，如果为0则执行SQL文件创建表
			if (DatabaseMetadataUtils.getDatabaseTableCount(urlMeta.getDatabaseName(), connection) == 0) {
				log.warn("数据库{}中没有任何表，将进行创建！", urlMeta.getDatabaseName());
				connection.setAutoCommit(true);
				SQLExecuteUtils.batchRunSQLScript(initializeProperties.getSqlPaths(), connection);
				log.info("初始化表格完成！");
			} else {
				log.info("数据库中已经存在表格，不需要创建！");
			}
		} catch (Exception e) {
			log.error("初始化表格时，连接数据库失败！");
			log.error(e.getMessage());
		}
	}

}