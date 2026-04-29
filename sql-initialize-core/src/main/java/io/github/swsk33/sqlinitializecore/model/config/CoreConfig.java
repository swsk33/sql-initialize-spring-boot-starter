package io.github.swsk33.sqlinitializecore.model.config;

import lombok.Builder;
import lombok.Value;

/**
 * SQL初始化核心配置。
 */
@Value
@Builder
public class CoreConfig {

	/**
	 * 是否启用SQL初始化检查。
	 */
	@Builder.Default
	boolean enabled = true;

	/**
	 * 数据源配置。
	 */
	DatasourceConfig datasource;

	/**
	 * 数据库检查配置。
	 */
	DatabaseCheckConfig databaseCheck;

	/**
	 * 表检查配置。
	 */
	TableCheckConfig tableCheck;

}