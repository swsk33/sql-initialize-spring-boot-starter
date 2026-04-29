package io.github.swsk33.sqlinitializecore.template.impl;

import io.github.swsk33.sqlinitializecore.strategy.DatabaseStructureCheckStrategy;
import io.github.swsk33.sqlinitializecore.strategy.context.DatabaseStructureCheckContext;
import io.github.swsk33.sqlinitializecore.template.AbstractDatabaseInitializeTemplate;

import static io.github.swsk33.sqlinitializecore.param.DatabasePlatformName.POSTGRE_SQL;

/**
 * PostgreSQL数据库初始化模板。
 */
public class PostgreSQLDatabaseInitializeTemplate extends AbstractDatabaseInitializeTemplate {

	@Override
	protected DatabaseStructureCheckStrategy getStructureCheckStrategy() {
		return DatabaseStructureCheckContext.getStrategy(POSTGRE_SQL);
	}

}