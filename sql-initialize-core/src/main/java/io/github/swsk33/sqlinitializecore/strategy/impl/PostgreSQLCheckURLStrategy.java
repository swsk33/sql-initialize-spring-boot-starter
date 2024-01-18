package io.github.swsk33.sqlinitializecore.strategy.impl;

import io.github.swsk33.sqlinitializecore.model.ConnectionMetadata;
import io.github.swsk33.sqlinitializecore.strategy.MetadataCheckURLStrategy;

/**
 * 用于生成PostgreSQL检测元数据时的连接地址的策略
 */
public class PostgreSQLCheckURLStrategy implements MetadataCheckURLStrategy {

	@Override
	public String getCheckConnectionURL(ConnectionMetadata urlMeta) {
		// PostgreSQL连接时必须指定一个数据库，连接至默认的postgres数据库
		return "jdbc:" + urlMeta.getDatabasePlatform() + "://" + urlMeta.getHostAndPort() + "/postgres";
	}

}