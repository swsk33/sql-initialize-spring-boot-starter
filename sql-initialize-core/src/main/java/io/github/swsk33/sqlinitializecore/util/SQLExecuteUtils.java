package io.github.swsk33.sqlinitializecore.util;

import cn.hutool.core.collection.CollectionUtil;
import io.github.swsk33.sqlinitializecore.strategy.context.CreateDatabaseContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

/**
 * SQL 文件执行的实用类
 */
@Slf4j
public class SQLExecuteUtils {

	/**
	 * classpath 路径前缀
	 */
	private static final String CLASSPATH_PREFIX = "classpath:";

	/**
	 * 文件系统路径前缀
	 */
	private static final String FILE_PREFIX = "file:";

	/**
	 * 创建一个数据库
	 *
	 * @param databasePlatform 数据库平台
	 * @param databaseName     要创建的数据库名
	 * @param connection       JDBC 连接
	 */
	public static void createDatabase(String databasePlatform, String databaseName, Connection connection) {
		try (Statement statement = connection.createStatement()) {
			// 根据不同的数据库平台生成 SQL 语句
			String createSQL = CreateDatabaseContext.generateCreateDatabaseSQL(databasePlatform, databaseName);
			// 若为null说明数据库平台不在支持范围，终止
			if (createSQL == null) {
				return;
			}
			// 执行
			statement.execute(createSQL);
			log.info("已创建数据库{}！", databaseName);
		} catch (Exception e) {
			log.error("创建数据库失败！");
			throw new RuntimeException(e);
		}
	}

	/**
	 * 读取并执行一个 SQL 脚本文件
	 *
	 * @param filepath   SQL脚本文件路径，需要以classpath:或者file:开头分别表示类路径和文件系统路径
	 * @param connection 执行脚本文件的连接
	 */
	public static void executeSQLScript(String filepath, Connection connection) {
		// 检查前缀是否正确
		if (!filepath.startsWith(CLASSPATH_PREFIX) && !filepath.startsWith(FILE_PREFIX)) {
			throw new RuntimeException("SQL文件前缀配置不正确！必须要以classpath:或者file:开头分别表示类路径和文件系统路径！");
		}
		Resource resource = filepath.startsWith(CLASSPATH_PREFIX) ? new ClassPathResource(filepath.replace(CLASSPATH_PREFIX, "")) : new FileSystemResource(filepath.replace(FILE_PREFIX, ""));
		try {
			ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
			populator.setSqlScriptEncoding("UTF-8");
			populator.setContinueOnError(false);
			populator.addScript(resource);
			populator.populate(connection);
		} catch (Exception e) {
			log.error("执行SQL文件失败：{}", filepath, e);
			throw new RuntimeException("执行SQL文件失败：" + filepath, e);
		}
	}

	/**
	 * 批量执行 SQL 脚本
	 *
	 * @param paths      SQL脚本的路径列表，其中classpath:开头的是位于类路径的，file:开头的是位于文件路径的
	 * @param connection 数据库连接对象，通过这个连接执行脚本
	 */
	public static void batchRunSQLScript(List<String> paths, Connection connection) {
		if (CollectionUtil.isEmpty(paths)) {
			log.warn("未配置初始化SQL脚本路径，跳过执行！");
			return;
		}
		for (String path : paths) {
			String trimPath = path.trim();
			log.info("执行文件：{}", trimPath);
			executeSQLScript(trimPath, connection);
		}
	}

}