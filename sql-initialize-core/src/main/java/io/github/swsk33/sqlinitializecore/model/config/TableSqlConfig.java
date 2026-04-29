package io.github.swsk33.sqlinitializecore.model.config;

import lombok.Builder;
import lombok.Value;

import java.util.List;

/**
 * 单张表的检查及SQL脚本配置。
 */
@Value
@Builder
public class TableSqlConfig {

	/**
	 * 表所属schema，PostgreSQL默认使用public，MySQL默认使用数据库名。
	 */
	String schemaName;

	/**
	 * 需要检查的表名。
	 */
	String tableName;

	/**
	 * 表不存在时执行的SQL脚本路径列表。
	 */
	List<String> sqlPaths;

}