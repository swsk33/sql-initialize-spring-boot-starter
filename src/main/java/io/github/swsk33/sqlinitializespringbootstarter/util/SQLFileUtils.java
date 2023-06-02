package io.github.swsk33.sqlinitializespringbootstarter.util;

import cn.hutool.core.io.resource.ClassPathResource;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.jdbc.ScriptRunner;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;

/**
 * SQL文件执行的实用类
 */
@Slf4j
public class SQLFileUtils {

	/**
	 * 执行单个SQL脚本
	 *
	 * @param path        SQL脚本文件的路径
	 * @param isClasspath SQL脚本路径是否是classpath路径
	 * @param connection  数据库连接对象，通过这个连接执行脚本
	 */
	private static void runSQLScript(String path, boolean isClasspath, Connection connection) {
		try (InputStream sqlFileStream = isClasspath ? new ClassPathResource(path).getStream() : Files.newInputStream(Paths.get(path))) {
			// 以UTF-8编码读取文件流
			BufferedReader sqlFileStreamReader = new BufferedReader(new InputStreamReader(sqlFileStream, StandardCharsets.UTF_8));
			// 创建脚本执行器实用类
			ScriptRunner scriptRunner = new ScriptRunner(connection);
			// 执行脚本
			scriptRunner.runScript(sqlFileStreamReader);
			// 最后关闭会话和文件读取器
			sqlFileStreamReader.close();
		} catch (Exception e) {
			log.error("读取文件或者执行脚本失败！");
			e.printStackTrace();
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
		final String classpathPrefix = "classpath:";
		final String filePathPrefix = "file:";
		for (String path : paths) {
			String trimPath = path.trim();
			log.info("执行文件：" + trimPath);
			if (trimPath.startsWith(classpathPrefix)) {
				runSQLScript(trimPath.replace(classpathPrefix, ""), true, connection);
			} else if (trimPath.startsWith(filePathPrefix)) {
				runSQLScript(trimPath.replace(filePathPrefix, ""), false, connection);
			} else {
				log.error("解析文件路径：" + trimPath + "错误！SQL文件必须配置为classpath:或者file:开头！");
			}
		}
	}

}