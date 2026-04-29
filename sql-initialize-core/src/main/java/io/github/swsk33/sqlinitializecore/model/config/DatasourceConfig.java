package io.github.swsk33.sqlinitializecore.model.config;

import lombok.Builder;
import lombok.Value;

/**
 * 数据源连接配置。
 */
@Value
@Builder
public class DatasourceConfig {

	/**
	 * JDBC连接地址。
	 */
	String url;

	/**
	 * 数据库用户名。
	 */
	String username;

	/**
	 * 数据库密码。
	 */
	String password;

}