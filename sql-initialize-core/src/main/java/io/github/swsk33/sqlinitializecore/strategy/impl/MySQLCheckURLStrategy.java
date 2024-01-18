package io.github.swsk33.sqlinitializecore.strategy.impl;

import io.github.swsk33.sqlinitializecore.model.ConnectionMetadata;
import io.github.swsk33.sqlinitializecore.strategy.MetadataCheckURLStrategy;

/**
 * 用于生成MySQL检测元数据时的连接地址的策略
 */
public class MySQLCheckURLStrategy implements MetadataCheckURLStrategy {

	@Override
	public String getCheckConnectionURL(ConnectionMetadata urlMeta) {
		// MySQL允许直接连接并且不指定任何数据库，直接返回
		return "jdbc:" + urlMeta.getDatabasePlatform() + "://" + urlMeta.getHostAndPort() + "/";
	}

}