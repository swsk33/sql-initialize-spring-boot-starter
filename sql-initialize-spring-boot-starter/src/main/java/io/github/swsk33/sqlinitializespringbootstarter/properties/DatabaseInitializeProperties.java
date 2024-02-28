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
	 * 是否在初始化表格之前，检查数据库是否存在并创建
	 * 如果设定为false，则会跳过数据库检查步骤，直接开始创建表格
	 * 在连接到ShardingSphere-Proxy分片代理时，建议设定该项为false
	 */
	private boolean checkDatabase = true;

	/**
	 * 用于初始化的SQL脚本路径列表
	 * 其中以classpath:开头表示文件位于classpath中
	 * 以file:开头表示文件位于文件系统
	 */
	private String[] sqlPaths;

}