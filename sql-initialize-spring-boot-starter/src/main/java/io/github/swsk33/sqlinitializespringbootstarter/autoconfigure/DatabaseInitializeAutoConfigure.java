package io.github.swsk33.sqlinitializespringbootstarter.autoconfigure;

import cn.hutool.core.collection.CollectionUtil;
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
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
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
public class DatabaseInitializeAutoConfigure implements InitializingBean {

	/**
	 * Spring 环境配置
	 */
	private final Environment environment;

	/**
	 * 获取读取的配置文件值
	 */
	private final DatabaseInitializeProperties initializeProperties;

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
	 * 构造器依赖自动注入
	 *
	 * @param environment          配置环境变量
	 * @param initializeProperties 初始化属性
	 */
	public DatabaseInitializeAutoConfigure(Environment environment, DatabaseInitializeProperties initializeProperties) {
		this.environment = environment;
		this.initializeProperties = initializeProperties;
	}

	/**
	 * 将列表转换为不可变列表集合，避免误操作修改配置
	 *
	 * @param source 源列表
	 * @return 不可变列表，若传入列表为null或空，则返回一个空列表
	 */
	private <T> List<T> toImmutableList(List<T> source) {
		if (CollectionUtil.isEmpty(source)) {
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
	private DatasourceConfig buildDatasourceConfig(Environment environment) {
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
	private CoreConfig buildCoreConfig(DatabaseInitializeProperties initializeProperties, DatasourceConfig datasourceConfig) {
		DatabaseInitializeProperties.DatabaseCheckProperties databaseCheckProperties = initializeProperties.getDatabaseCheck();
		DatabaseInitializeProperties.TableCheckProperties tableCheckProperties = initializeProperties.getTableCheck();
		// 构建基本核心属性
		CoreConfig.CoreConfigBuilder coreConfigBuilder = CoreConfig.builder()
				.enabled(initializeProperties.isEnabled())
				.datasource(datasourceConfig);
		// 构建数据库初始化属性
		if (databaseCheckProperties != null) {
			// 构建并组装至核心属性
			coreConfigBuilder.databaseCheck(
					DatabaseCheckConfig.builder()
							.checkDatabase(databaseCheckProperties.isCheckDatabase())
							.sqlPaths(toImmutableList(databaseCheckProperties.getSqlPaths()))
							.build()
			);
		}
		// 构建表初始化属性
		if (tableCheckProperties != null) {
			// 构建属性
			TableCheckConfig.TableCheckConfigBuilder tableCheckConfigBuilder = TableCheckConfig.builder()
					.checkTable(tableCheckProperties.isCheckTable());
			// 特定表的初始化属性
			if (!CollectionUtil.isEmpty(tableCheckProperties.getTableList())) {
				List<TableSqlConfig> tableList = new ArrayList<>();
				for (DatabaseInitializeProperties.TableSqlProperties tableProperties : tableCheckProperties.getTableList()) {
					tableList.add(TableSqlConfig.builder()
							.schemaName(tableProperties.getSchemaName())
							.tableName(tableProperties.getTableName())
							.sqlPaths(tableProperties.getSqlPaths())
							.build());
				}
				tableCheckConfigBuilder.tableList(toImmutableList(tableList));
			}
			// 组装到核心属性
			coreConfigBuilder.tableCheck(tableCheckConfigBuilder.build());
		}
		return coreConfigBuilder.build();
	}

	/**
	 * 用于检查和初始化整个数据库的方法。
	 */
	@Override
	public void afterPropertiesSet() {
		log.info("------- SQL自动初始化开始自动配置φ(>ω<*)  -------");
		if (!initializeProperties.isEnabled()) {
			log.warn("SQL自动初始化已禁用！将不会进行数据库检查和初始化操作！");
			return;
		}
		DatasourceConfig datasourceConfig = buildDatasourceConfig(environment);
		CoreConfig coreConfig = buildCoreConfig(initializeProperties, datasourceConfig);
		AbstractDatabaseInitializeTemplate initializeTemplate = DatabaseInitializeTemplateContext.getTemplate(coreConfig);
		if (initializeTemplate == null) {
			throw new IllegalStateException("暂不支持当前数据源对应的数据库初始化模板！");
		}
		initializeTemplate.runInitialize(coreConfig);
	}

}