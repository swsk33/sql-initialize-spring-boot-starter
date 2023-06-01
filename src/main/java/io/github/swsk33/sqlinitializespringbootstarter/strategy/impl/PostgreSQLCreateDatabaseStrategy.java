package io.github.swsk33.sqlinitializespringbootstarter.strategy.impl;

import io.github.swsk33.sqlinitializespringbootstarter.strategy.CreateDatabaseStrategy;

/**
 * 创建数据库策略-PostgreSQL
 */
public class PostgreSQLCreateDatabaseStrategy implements CreateDatabaseStrategy {

	@Override
	public String generateCreateDatabaseSQL(String databaseName) {
		return "create database \"" + databaseName + "\"";
	}

}