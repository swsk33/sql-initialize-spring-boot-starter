package io.github.swsk33.sqlinitializespringbootstarter.autoconfigure;

import cn.hutool.core.util.StrUtil;
import io.github.swsk33.sqlinitializecore.model.ConnectionMetadata;
import io.github.swsk33.sqlinitializecore.strategy.context.MetaCheckURLContext;
import io.github.swsk33.sqlinitializecore.util.DatabaseMetadataUtils;
import io.github.swsk33.sqlinitializecore.util.SQLExecuteUtils;
import io.github.swsk33.sqlinitializespringbootstarter.properties.DatabaseInitializeProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * 数据库初始化的自动配置类
 */
@Slf4j
@Configuration("SQLInitializeAutoConfigure")
@EnableConfigurationProperties({DataSourceProperties.class, DatabaseInitializeProperties.class})
public class DatabaseInitializeAutoConfigure implements InitializingBean {

	/**
	 * 获取Spring Boot数据源
	 */
	@Autowired
	private DataSourceProperties dataSourceProperties;

	/**
	 * 获取读取的配置文件值
	 */
	@Autowired
	private DatabaseInitializeProperties initializeProperties;

	/**
	 * 解析数据源信息
	 *
	 * @return 解析后JDBC数据源信息
	 */
	private ConnectionMetadata parseDatasource() {
		ConnectionMetadata metadata = new ConnectionMetadata(dataSourceProperties.getUrl());
		log.info("已完成对配置数据库地址的解析！");
		log.info("数据库软件平台：{}", metadata.getDatabasePlatform());
		return metadata;
	}

	/**
	 * 检查数据库是否存在，不存在则创建
	 *
	 * @param metadata 传入已解析数据源信息
	 * @return 是否创建成功
	 */
	private boolean checkDatabase(ConnectionMetadata metadata) {
		if (!initializeProperties.isCheckDatabase()) {
			log.warn("已禁用数据库检查！将不会检查数据库是否存在，直接进行表格创建操作！");
			return true;
		}
		log.info("开始检查数据库是否需要初始化...");
		// 组装地址并重新连接
		// 根据策略获取不同数据库平台对应的检测连接地址
		String checkUrl = MetaCheckURLContext.getCheckURL(metadata);
		// 获取失败则退出
		if (StrUtil.isEmpty(checkUrl)) {
			return false;
		}
		try (Connection connection = DriverManager.getConnection(checkUrl, dataSourceProperties.getUsername(), dataSourceProperties.getPassword())) {
			// 检测数据库是否存在
			if (!DatabaseMetadataUtils.databaseExists(metadata.getDatabaseName(), connection)) {
				log.warn("数据库不存在！准备创建！");
				return SQLExecuteUtils.createDatabase(metadata.getDatabasePlatform(), metadata.getDatabaseName(), connection);
			} else {
				log.info("数据库存在，不需要创建！");
				return true;
			}
		} catch (Exception e) {
			log.error("连接至数据库检查元数据时失败！");
			log.error(e.getMessage());
		}
		return false;
	}

	/**
	 * 初始化数据库中表格
	 *
	 * @param metadata 传入已解析数据源信息
	 */
	private void createTables(ConnectionMetadata metadata) {
		// 然后再次连接，执行脚本初始化库中的表格
		try (Connection connection = DriverManager.getConnection(dataSourceProperties.getUrl(), dataSourceProperties.getUsername(), dataSourceProperties.getPassword())) {
			// 检测数据库中表的数量，如果为0则执行SQL文件创建表
			if (DatabaseMetadataUtils.getDatabaseTableCount(metadata.getDatabaseName(), connection) == 0) {
				log.warn("数据库{}中没有任何表，将进行创建！", metadata.getDatabaseName());
				connection.setAutoCommit(true);
				SQLExecuteUtils.batchRunSQLScript(initializeProperties.getSqlPaths(), connection);
				log.info("初始化表格完成！");
			} else {
				log.info("数据库中已经存在表格，不需要创建！");
			}
		} catch (Exception e) {
			log.error("初始化表格时，连接数据库失败！");
			log.error(e.getMessage());
		}
	}

	/**
	 * 用于检查和初始化整个数据库的方法
	 */
	@Override
	public void afterPropertiesSet() {
		log.info("------- SQL自动初始化开始自动配置φ(>ω<*)  -------");
		if (!initializeProperties.isEnabled()) {
			log.warn("SQL自动初始化已禁用！将不会进行数据库检查和初始化操作！");
			return;
		}
		// 执行流程
		ConnectionMetadata metadata = parseDatasource();
		if (checkDatabase(metadata)) {
			createTables(metadata);
		}
	}

}