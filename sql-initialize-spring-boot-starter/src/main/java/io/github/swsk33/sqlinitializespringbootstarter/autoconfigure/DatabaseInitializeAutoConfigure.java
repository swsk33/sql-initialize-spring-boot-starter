package io.github.swsk33.sqlinitializespringbootstarter.autoconfigure;

import cn.hutool.core.util.StrUtil;
import io.github.swsk33.sqlinitializecore.model.config.CoreConfig;
import io.github.swsk33.sqlinitializecore.model.config.DatabaseCheckConfig;
import io.github.swsk33.sqlinitializecore.model.config.DatasourceConfig;
import io.github.swsk33.sqlinitializecore.model.config.TableCheckConfig;
import io.github.swsk33.sqlinitializecore.model.config.TableSqlConfig;
import io.github.swsk33.sqlinitializecore.template.AbstractDatabaseInitializeTemplate;
import io.github.swsk33.sqlinitializecore.template.context.DatabaseInitializeTemplateContext;
import io.github.swsk33.sqlinitializespringbootstarter.properties.DatabaseInitializeProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 数据库初始化的自动配置类
 */
@Slf4j
@Configuration("SQLInitializeAutoConfigure")
@EnableConfigurationProperties(DatabaseInitializeProperties.class)
public class DatabaseInitializeAutoConfigure {

	/**
	 * Spring Boot数据源连接地址配置键
	 */
	private static final String DATASOURCE_URL_KEY = "spring.datasource.url";

	/**
	 * Spring Boot数据源用户名配置键
	 */
	private static final String DATASOURCE_USERNAME_KEY = "spring.datasource.username";

	/**
	 * Spring Boot数据源密码配置键
	 */
	private static final String DATASOURCE_PASSWORD_KEY = "spring.datasource.password";

	/**
	 * 复制列表，避免核心配置持有Spring Boot绑定对象的可变集合
	 *
	 * @param source 源列表
	 * @return 不可变列表
	 */
	private List<String> copyList(List<String> source) {
		if (source == null) {
			return null;
		}
		if (source.isEmpty()) {
			return Collections.emptyList();
		}
		return Collections.unmodifiableList(new ArrayList<>(source));
	}

	/**
	 * 从 Environment 读取数据源配置
	 *
	 * @param environment 配置环境变量
	 * @return 数据源配置
	 */
	@Bean
	public DatasourceConfig buildDatasourceConfig(Environment environment) {
		String datasourceUrl = environment.getProperty(DATASOURCE_URL_KEY);
		if (StrUtil.isEmpty(datasourceUrl)) {
			throw new IllegalStateException("未配置数据源连接地址：" + DATASOURCE_URL_KEY);
		}
		return DatasourceConfig.builder()
				.url(datasourceUrl)
				.username(environment.getProperty(DATASOURCE_USERNAME_KEY))
				.password(environment.getProperty(DATASOURCE_PASSWORD_KEY))
				.build();
	}

	/**
	 * 将Spring Boot可变绑定属性转换为核心不可变配置
	 *
	 * @param initializeProperties 读取的配置文件值
	 * @return 核心配置
	 */
	@Bean
	public CoreConfig buildCoreConfig(DatabaseInitializeProperties initializeProperties, DatasourceConfig datasourceConfig) {
		DatabaseInitializeProperties.DatabaseCheckProperties databaseCheckProperties = initializeProperties.getDatabaseCheck();
		DatabaseInitializeProperties.TableCheckProperties tableCheckProperties = initializeProperties.getTableCheck();
		CoreConfig.CoreConfigBuilder coreConfigBuilder = CoreConfig.builder()
				.enabled(initializeProperties.isEnabled())
				.datasource(datasourceConfig);
		if (databaseCheckProperties != null) {
			coreConfigBuilder.databaseCheck(DatabaseCheckConfig.builder()
					.checkDatabase(databaseCheckProperties.isCheckDatabase())
					.sqlPaths(copyList(databaseCheckProperties.getSqlPaths()))
					.build());
		}
		if (tableCheckProperties == null) {
			return coreConfigBuilder.build();
		}
		List<TableSqlConfig> tableList = null;
		if (tableCheckProperties.getTableList() != null) {
			tableList = new ArrayList<>();
			for (DatabaseInitializeProperties.TableSqlProperties tableProperties : tableCheckProperties.getTableList()) {
				tableList.add(TableSqlConfig.builder()
						.schemaName(tableProperties.getSchemaName())
						.tableName(tableProperties.getTableName())
						.sqlPaths(copyList(tableProperties.getSqlPaths()))
						.build());
			}
		}
		return coreConfigBuilder.tableCheck(TableCheckConfig.builder()
				.checkTable(tableCheckProperties.isCheckTable())
				.tableList(tableList == null ? null : Collections.unmodifiableList(tableList))
				.build()).build();
	}

	/**
	 * 用于检查和初始化整个数据库的方法
	 *
	 * @param coreConfig 注入已构建的核心配置
	 */
	@Bean
	public ApplicationRunner checkAndInitialize(CoreConfig coreConfig) {
		return args -> {
			// 获取模板执行
			AbstractDatabaseInitializeTemplate initializeTemplate = DatabaseInitializeTemplateContext.getTemplate(coreConfig);
			if (initializeTemplate == null) {
				throw new IllegalStateException("暂不支持当前数据源对应的数据库初始化模板！");
			}
			initializeTemplate.runInitialize(coreConfig);
		};
	}

}