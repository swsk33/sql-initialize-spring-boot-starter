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

	/**
	 * 存放不同数据库平台对应的“找不到数据库”错误码，不同数据库驱动连接数据库时找不到数据库的错误码不同，例如MySQL是1049
	 * 键表示数据库平台，例如mysql
	 * 值表示这个平台的驱动抛出“找不到数据库”异常时的错误码
	 */
	public static final Map<String, Integer> DATABASE_NOT_EXIST_ERROR_CODE = new HashMap<>();

	// 初始化所有的数据库创建语句和平台错误码
	static {
		CREATE_DATABASE_SQL.put(DatabasePlatformName.MYSQL, new MySQLCreateDatabaseStrategy());
		CREATE_DATABASE_SQL.put(DatabasePlatformName.POSTGRE_SQL, new PostgreSQLCreateDatabaseStrategy());
		log.info("所有数据库创建策略初始化完成！");
		DATABASE_NOT_EXIST_ERROR_CODE.put(DatabasePlatformName.MYSQL, 1049);
		DATABASE_NOT_EXIST_ERROR_CODE.put(DatabasePlatformName.POSTGRE_SQL, 0);
		log.info("错误码列表初始化完成！");
	}

	/**
	 * 根据传入数据库平台和创建的数据库名生成SQL语句
	 *
	 * @param databasePlatform 数据库平台，例如mysql
	 * @param databaseName     待创建的数据库名
	 * @return 生成的SQL语句
	 */
	public static String generateCreateDatabaseSQL(String databasePlatform, String databaseName) {
		if (!CREATE_DATABASE_SQL.containsKey(databasePlatform)) {
			log.error("暂时不支持数据库平台：" + databasePlatform + " 的初始化！");
			return null;
		}
		return CREATE_DATABASE_SQL.get(databasePlatform).generateCreateDatabaseSQL(databaseName);
	}

}