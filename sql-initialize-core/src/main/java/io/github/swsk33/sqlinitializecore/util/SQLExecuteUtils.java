package io.github.swsk33.sqlinitializecore.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import io.github.swsk33.sqlinitializecore.strategy.context.CreateDatabaseContext;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.Statement;

/**
 * SQL文件执行的实用类
 */
@Slf4j
public class SQLExecuteUtils {

	/**
	 * classpath路径前缀
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
	 * @param connection       JDBC连接
	 */
	public static void createDatabase(String databasePlatform, String databaseName, Connection connection) {
		try (Statement statement = connection.createStatement()) {
			// 根据不同的数据库平台生成SQL语句
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
			log.error(e.getMessage());
		}
	}

	/**
	 * 读取并执行一个SQL脚本文件
	 *
	 * @param filepath   SQL脚本文件路径，需要以classpath:或者file:开头分别表示类路径和文件系统路径
	 * @param connection 执行脚本文件的连接
	 */
	public static void executeSQLScript(String filepath, Connection connection) {
		// 检查前缀是否正确
		if (!filepath.startsWith(CLASSPATH_PREFIX) && !filepath.startsWith(FILE_PREFIX)) {
			throw new RuntimeException("SQL文件前缀配置不正确！必须要以classpath:或者file:开头分别表示类路径和文件系统路径！");
		}
		// 根据不同文件前缀判断是从classpath或者文件系统读取文件流
		try (InputStream sqlFileStream = filepath.startsWith(CLASSPATH_PREFIX) ? new ClassPathResource(filepath.replace(CLASSPATH_PREFIX, "")).getStream() : FileUtil.getInputStream(Paths.get(filepath.replace(FILE_PREFIX, "")).toAbsolutePath().toString()); Statement statement = connection.createStatement()) {
			// 以UTF-8编码读取文件流
			BufferedReader sqlFileStreamReader = new BufferedReader(new InputStreamReader(sqlFileStream, StandardCharsets.UTF_8));
			// 读取到的行
			String line;
			// 每次执行的语句
			StringBuilder sqlExecute = new StringBuilder();
			// 开始读取
			while ((line = sqlFileStreamReader.readLine()) != null) {
				// 去除两端不可见字符
				line = line.trim();
				// 跳过注释或者空字符行
				if (line.isEmpty() || line.startsWith("--") || line.startsWith("#")) {
					continue;
				}
				// 将语句追加至每次执行的语句
				sqlExecute.append(line).append(System.lineSeparator());
				// 如果当前语句以分号结尾，说明已经得到了一个完整的SQL语句，执行
				if (line.endsWith(";")) {
					statement.execute(sqlExecute.toString());
					System.out.println(sqlExecute);
					// 清空语句以便下一条语句的追加
					sqlExecute.setLength(0);
				}
			}
			// 若读取完成后，每次执行的语句中还有语句，则继续执行
			if (!sqlExecute.isEmpty()) {
				statement.execute(sqlExecute.toString());
				System.out.println(sqlExecute);
			}
		} catch (Exception e) {
			log.error("执行SQL文件失败！");
			log.error(e.getMessage());
		}
	}

	/**
	 * 批量执行SQL脚本
	 *
	 * @param paths      SQL脚本的路径数组，其中classpath:开头的是位于类路径的，file:开头的是位于文件路径的
	 * @param connection 数据库连接对象，通过这个连接执行脚本
	 */
	public static void batchRunSQLScript(String[] paths, Connection connection) {
		if (paths == null) {
			log.warn("未配置初始化表的SQL脚本路径！只进行数据库创建！");
			return;
		}
		for (String path : paths) {
			String trimPath = path.trim();
			log.info("执行文件：" + trimPath);
			executeSQLScript(path, connection);
		}
	}

}