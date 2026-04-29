package io.github.swsk33.sqlinitializecore.strategy.context;

import io.github.swsk33.sqlinitializecore.param.DatabasePlatformName;
import io.github.swsk33.sqlinitializecore.strategy.DatabaseStructureCheckStrategy;
import io.github.swsk33.sqlinitializecore.strategy.impl.MySQLStructureCheckStrategy;
import io.github.swsk33.sqlinitializecore.strategy.impl.PostgreSQLStructureCheckStrategy;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * 数据库结构检查策略上下文。
 */
@Slf4j
public class DatabaseStructureCheckContext {

	/**
	 * 存放不同数据库平台的数据库结构检查策略
	 */
	private static final Map<String, DatabaseStructureCheckStrategy> STRATEGY_MAP = new HashMap<>();

	static {
		STRATEGY_MAP.put(DatabasePlatformName.MYSQL, new MySQLStructureCheckStrategy());
		STRATEGY_MAP.put(DatabasePlatformName.POSTGRE_SQL, new PostgreSQLStructureCheckStrategy());
	}

	/**
	 * 根据数据库平台获取结构检查策略。
	 *
	 * @param databasePlatform 数据库平台
	 * @return 检查策略，不支持时返回null
	 */
	public static DatabaseStructureCheckStrategy getStrategy(String databasePlatform) {
		String platform = databasePlatform.toLowerCase();
		if (!STRATEGY_MAP.containsKey(platform)) {
			log.error("暂时不支持数据库平台：{}的结构检查！", platform);
			return null;
		}
		return STRATEGY_MAP.get(platform);
	}

}