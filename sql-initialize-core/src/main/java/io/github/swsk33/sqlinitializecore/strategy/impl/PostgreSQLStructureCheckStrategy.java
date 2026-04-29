package io.github.swsk33.sqlinitializecore.strategy.impl;

import cn.hutool.core.util.StrUtil;
import io.github.swsk33.sqlinitializecore.model.ConnectionMetadata;
import io.github.swsk33.sqlinitializecore.strategy.DatabaseStructureCheckStrategy;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * PostgreSQL数据库结构检查策略。
 */
@Slf4j
public class PostgreSQLStructureCheckStrategy implements DatabaseStructureCheckStrategy {

	/**
	 * 默认 Schema 名称
	 */
	private static final String DEFAULT_SCHEMA = "public";

	@Override
	public String getCheckConnectionURL(ConnectionMetadata metadata) {
		return metadata.toConnectionUrl("postgres");
	}

	@Override
	public boolean databaseExists(String databaseName, Connection connection) {
		String sql = "select 1 from pg_database where datname = ?";
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, databaseName);
			try (ResultSet resultSet = statement.executeQuery()) {
				return resultSet.next();
			}
		} catch (Exception e) {
			log.error("检查PostgreSQL数据库是否存在失败！", e);
			return false;
		}
	}

	@Override
	public boolean tableExists(String databaseName, String schemaName, String tableName, Connection connection) {
		String actualSchemaName = StrUtil.isEmpty(schemaName) ? DEFAULT_SCHEMA : schemaName;
		String sql = "select 1 from information_schema.tables where table_schema = ? and table_name = ?";
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, actualSchemaName);
			statement.setString(2, tableName);
			try (ResultSet resultSet = statement.executeQuery()) {
				return resultSet.next();
			}
		} catch (Exception e) {
			log.error("检查PostgreSQL数据表是否存在失败！", e);
			return false;
		}
	}

}