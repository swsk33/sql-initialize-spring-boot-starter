package io.github.swsk33.sqlinitializecore.strategy.context;

import io.github.swsk33.sqlinitializecore.param.DatabasePlatformName;
import io.github.swsk33.sqlinitializecore.strategy.CreateDatabaseStrategy;
import io.github.swsk33.sqlinitializecore.strategy.impl.MySQLCreateDatabaseStrategy;
import io.github.swsk33.sqlinitializecore.strategy.impl.PostgreSQLCreateDatabaseStrategy;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * 创建数据库策略的上下文
 */
@Slf4j
public class CreateDatabaseContext {

	/**
	 * 存放不同数据库平台中用于创建数据库的SQL语句
	 * 键是数据库平台，例如mysql
	 * 值是对应的创建数据库策略
	 */
	private static final Map<String, CreateDatabaseStrategy> CREATE_DATABASE_SQL = new HashMap<>();

	// 初始化所有的数据库创建语句和平台错误码
	static {
		CREATE_DATABASE_SQL.put(DatabasePlatformName.MYSQL, new MySQLCreateDatabaseStrategy());
		CREATE_DATABASE_SQL.put(DatabasePlatformName.POSTGRE_SQL, new PostgreSQLCreateDatabaseStrategy());
	}

	/**
	 * 根据传入数据库平台和创建的数据库名生成SQL语句
	 *
	 * @param databasePlatform 数据库平台，例如mysql
	 * @param databaseName     待创建的数据库名
	 * @return 生成的SQL语句
	 */
	public static String generateCreateDatabaseSQL(String databasePlatform, String databaseName) {
		databasePlatform = databasePlatform.toLowerCase();
		if (!CREATE_DATABASE_SQL.containsKey(databasePlatform)) {
			log.error("暂时不支持数据库平台：{}的初始化！", databasePlatform);
			return null;
		}
		return CREATE_DATABASE_SQL.get(databasePlatform).generateCreateDatabaseSQL(databaseName);
	}

}