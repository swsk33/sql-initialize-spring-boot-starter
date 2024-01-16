package io.github.swsk33.sqlinitializecore.model;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;

/**
 * 数据库连接的相关信息
 * 用于表示一个JDBC连接地址中每个部分例如地址和端口、库名等等
 */
@Data
@Slf4j
public class ConnectionMetadata {

	/**
	 * 数据库平台名，例如：mysql
	 */
	private String databasePlatform;

	/**
	 * 连接地址以及端口，例如：127.0.0.1:3306
	 */
	private String hostAndPort;

	/**
	 * 连接到的数据库名
	 */
	private String databaseName;

	/**
	 * 从一个完整的JDBC连接地址构造连接信息对象
	 *
	 * @param jdbcUrl JDBC连接地址
	 */
	public ConnectionMetadata(String jdbcUrl) {
		try {
			// 解析连接地址
			URI databaseURI = new URI(jdbcUrl.replace("jdbc:", ""));
			// 获取平台名
			databasePlatform = databaseURI.getScheme();
			// 获取地址端口
			hostAndPort = databaseURI.getAuthority();
			// 获取库名
			databaseName = databaseURI.getPath().substring(1);
		} catch (Exception e) {
			log.error("解析JDBC地址出错！");
			log.error(e.getMessage());
		}
	}

}