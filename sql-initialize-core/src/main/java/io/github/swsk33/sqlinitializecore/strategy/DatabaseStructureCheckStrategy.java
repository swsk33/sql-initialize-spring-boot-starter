package io.github.swsk33.sqlinitializecore.strategy;

import io.github.swsk33.sqlinitializecore.model.ConnectionMetadata;

import java.sql.Connection;

/**
 * 数据库结构检查策略，按数据库平台实现库和表的存在性判断。
 */
public interface DatabaseStructureCheckStrategy {

	/**
	 * 获取用于检查数据库是否存在的连接地址。
	 *
	 * @param metadata 已解析的连接元数据
	 * @return 检查连接地址
	 */
	String getCheckConnectionURL(ConnectionMetadata metadata);

	/**
	 * 判断指定数据库是否存在。
	 *
	 * @param databaseName 数据库名
	 * @param connection   检查连接
	 * @return 数据库是否存在
	 */
	boolean databaseExists(String databaseName, Connection connection);

	/**
	 * 判断指定表是否存在。
	 *
	 * @param databaseName 数据库名
	 * @param schemaName   schema名，可为空
	 * @param tableName    表名
	 * @param connection   目标数据库连接
	 * @return 表是否存在
	 */
	boolean tableExists(String databaseName, String schemaName, String tableName, Connection connection);

}