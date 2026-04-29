package io.github.swsk33.sqlinitializecore.template.context;

import io.github.swsk33.sqlinitializecore.model.ConnectionMetadata;
import io.github.swsk33.sqlinitializecore.model.config.CoreConfig;
import io.github.swsk33.sqlinitializecore.model.config.DatasourceConfig;
import io.github.swsk33.sqlinitializecore.param.DatabasePlatformName;
import io.github.swsk33.sqlinitializecore.template.AbstractDatabaseInitializeTemplate;
import io.github.swsk33.sqlinitializecore.template.impl.MySQLDatabaseInitializeTemplate;
import io.github.swsk33.sqlinitializecore.template.impl.PostgreSQLDatabaseInitializeTemplate;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * 数据库初始化模板上下文。
 */
@Slf4j
public class DatabaseInitializeTemplateContext {

	/**
	 * 存放不同数据库平台的初始化模板
	 */
	private static final Map<String, AbstractDatabaseInitializeTemplate> TEMPLATE_MAP = new HashMap<>();

	static {
		TEMPLATE_MAP.put(DatabasePlatformName.MYSQL, new MySQLDatabaseInitializeTemplate());
		TEMPLATE_MAP.put(DatabasePlatformName.POSTGRE_SQL, new PostgreSQLDatabaseInitializeTemplate());
	}

	/**
	 * 根据数据源配置获取初始化模板
	 *
	 * @param coreConfig 核心配置
	 * @return 初始化模板，不支持时返回null
	 */
	public static AbstractDatabaseInitializeTemplate getTemplate(CoreConfig coreConfig) {
		// 获取并校验数据源配置
		DatasourceConfig datasourceConfig = coreConfig.getDatasource();
		if (datasourceConfig == null) {
			log.error("未配置数据源，无法获取初始化模板！");
			return null;
		}
		// 解析连接元数据
		ConnectionMetadata metadata = new ConnectionMetadata(datasourceConfig.getUrl());
		// 获取数据库平台
		String platform = metadata.getDatabasePlatform().toLowerCase();
		if (!TEMPLATE_MAP.containsKey(platform)) {
			log.error("暂时不支持数据库平台：{}的初始化模板！", platform);
			return null;
		}
		return TEMPLATE_MAP.get(platform);
	}

}