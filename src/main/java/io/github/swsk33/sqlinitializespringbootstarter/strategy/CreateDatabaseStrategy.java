package io.github.swsk33.sqlinitializespringbootstarter.strategy;

/**
 * 用于生成创建数据库SQL的策略
 */
public interface CreateDatabaseStrategy {

	/**
	 * 生成创建数据库的SQL
	 *
	 * @param databaseName 创建的数据库名
	 * @return 创建数据库的SQL语句
	 */
	String generateCreateDatabaseSQL(String databaseName);

}