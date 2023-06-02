package io.github.swsk33.sqlinitializespringbootstarter.autoconfigure;

import io.github.swsk33.sqlinitializespringbootstarter.strategy.CreateDatabaseStrategy;
import io.github.swsk33.sqlinitializespringbootstarter.strategy.impl.MySQLCreateDatabaseStrategy;
import io.github.swsk33.sqlinitializespringbootstarter.strategy.impl.PostgreSQLCreateDatabaseStrategy;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;


import java.util.HashMap;
import java.util.Map;

/**
 * 不同的数据库平台创建策略配置类
 */
@Slf4j
@Configuration
public class DatabaseCreateStrategyAutoConfigure {

	/**
	 * 存放不同数据库平台中用于创建数据库的SQL语句
	 * 键是数据库平台，例如mysql
	 * 值是对应的创建数据库策略
	 */
	private static final Map<String, CreateDatabaseStrategy> CREATE_DATABASE_SQL = new HashMap<>();

	/**
	 * 初始化所有的数据库创建语句的方法
	 */
	@PostConstruct
	private void initStrategy() {
		CREATE_DATABASE_SQL.put("mysql", new MySQLCreateDatabaseStrategy());
		CREATE_DATABASE_SQL.put("postgresql", new PostgreSQLCreateDatabaseStrategy());
		log.info("所有数据库创建策略初始化完成！");
	}

	/**
	 * 根据传入数据库平台和创建的数据库名生成SQL语句
	 *
	 * @param databasePlatform 数据库平台，例如mysql
	 * @param databaseName     待创建的数据库名
	 * @return 生成的SQL语句
	 */
	public String generateSQL(String databasePlatform, String databaseName) {
		if (!CREATE_DATABASE_SQL.containsKey(databasePlatform)) {
			log.error("暂时不支持数据库平台：" + databasePlatform + " 的初始化！");
			return null;
		}
		return CREATE_DATABASE_SQL.get(databasePlatform).generateCreateDatabaseSQL(databaseName);
	}

}