package io.github.swsk33.sqlinitializecore.strategy.impl;

import io.github.swsk33.sqlinitializecore.model.ConnectionMetadata;
import io.github.swsk33.sqlinitializecore.strategy.DatabaseStructureCheckStrategy;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * MySQL数据库结构检查策略。
 */
@Slf4j
public class MySQLStructureCheckStrategy implements DatabaseStructureCheckStrategy {

	@Override
	public String getCheckConnectionURL(ConnectionMetadata metadata) {
		return metadata.toConnectionUrl(null);
	}

	@Override
	public boolean databaseExists(String databaseName, Connection connection) {
		String sql = "select 1 from information_schema.schemata where schema_name = ?";
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, databaseName);
			try (ResultSet resultSet = statement.executeQuery()) {
				return resultSet.next();
			}
		} catch (Exception e) {
			log.error("检查MySQL数据库是否存在失败！", e);
			return false;
		}
	}

	@Override
	public boolean tableExists(String databaseName, String schemaName, String tableName, Connection connection) {
		String sql = "select 1 from information_schema.tables where table_schema = ? and table_name = ?";
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, databaseName);
			statement.setString(2, tableName);
			try (ResultSet resultSet = statement.executeQuery()) {
				return resultSet.next();
			}
		} catch (Exception e) {
			log.error("检查MySQL数据表是否存在失败！", e);
			return false;
		}
	}

}