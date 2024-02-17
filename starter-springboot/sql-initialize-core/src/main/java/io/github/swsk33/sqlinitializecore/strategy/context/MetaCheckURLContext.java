package io.github.swsk33.sqlinitializecore.strategy.context;

import io.github.swsk33.sqlinitializecore.model.ConnectionMetadata;
import io.github.swsk33.sqlinitializecore.param.DatabasePlatformName;
import io.github.swsk33.sqlinitializecore.strategy.MetadataCheckURLStrategy;
import io.github.swsk33.sqlinitializecore.strategy.impl.MySQLCheckURLStrategy;
import io.github.swsk33.sqlinitializecore.strategy.impl.PostgreSQLCheckURLStrategy;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * 生成检测元数据时的连接地址的策略的上下文
 */
@Slf4j
public class MetaCheckURLContext {

	/**
	 * 策略对象列表
	 * 键是数据库平台，例如mysql
	 * 值是对应的元数据检测连接地址生成策略
	 */
	private static final Map<String, MetadataCheckURLStrategy> CHECK_URL_STRATEGY_MAP = new HashMap<>();

	// 初始化全部的连接URL生成策略
	static {
		CHECK_URL_STRATEGY_MAP.put(DatabasePlatformName.MYSQL, new MySQLCheckURLStrategy());
		CHECK_URL_STRATEGY_MAP.put(DatabasePlatformName.POSTGRE_SQL, new PostgreSQLCheckURLStrategy());
	}

	/**
	 * 根据不同数据库平台获取检测URL
	 *
	 * @param urlMeta 解析的连接URL
	 * @return 检测连接地址
	 */
	public static String getCheckURL(ConnectionMetadata urlMeta) {
		String platform = urlMeta.getDatabasePlatform().toLowerCase();
		if (!CHECK_URL_STRATEGY_MAP.containsKey(platform)) {
			log.error("暂时不支持数据库平台：{}的元数据读取！", platform);
			return null;
		}
		return CHECK_URL_STRATEGY_MAP.get(platform).getCheckConnectionURL(urlMeta);
	}

}