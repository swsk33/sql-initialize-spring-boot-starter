package io.github.swsk33.sqlinitializespringbootstarter.strategy.impl;

import io.github.swsk33.sqlinitializespringbootstarter.strategy.CreateDatabaseStrategy;

/**
 * 创建数据库策略-MySQL
 */
public class MySQLCreateDatabaseStrategy implements CreateDatabaseStrategy {

	@Override
	public String generateCreateDatabaseSQL(String databaseName) {
		return "create database if not exists `" + databaseName + "`";
	}

}