package io.github.swsk33.sqlinitializespringbootstarter.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 配置信息
 */
@Data
@ConfigurationProperties(prefix = "io.github.swsk33.sql-init")
public class DatabaseInitializeProperties {

	/**
	 * 是否启用SQL初始化，若为false则不会进行数据库初始化操作
	 */
	private boolean enabled = true;

	/**
	 * 用于初始化的SQL脚本路径列表
	 * 其中以classpath:开头表示文件位于classpath中
	 * 以file:开头表示文件位于文件系统
	 */
	private String[] sqlPaths;

}