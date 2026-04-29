package io.github.swsk33.sqlinitializecore.template;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import io.github.swsk33.sqlinitializecore.model.ConnectionMetadata;
import io.github.swsk33.sqlinitializecore.model.config.CoreConfig;
import io.github.swsk33.sqlinitializecore.model.config.DatabaseCheckConfig;
import io.github.swsk33.sqlinitializecore.model.config.DatasourceConfig;
import io.github.swsk33.sqlinitializecore.model.config.TableCheckConfig;
import io.github.swsk33.sqlinitializecore.model.config.TableSqlConfig;
import io.github.swsk33.sqlinitializecore.strategy.DatabaseStructureCheckStrategy;
import io.github.swsk33.sqlinitializecore.util.SQLExecuteUtils;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;

/**
 * 数据库初始化抽象模板，固定初始化流程，数据库差异由子类提供。
 */
@Slf4j
public abstract class AbstractDatabaseInitializeTemplate {

	/**
	 * 0. 获取当前数据库平台的结构检查策略
	 *
	 * @return 数据库结构检查策略
	 */
	protected abstract DatabaseStructureCheckStrategy getStructureCheckStrategy();

	/**
	 * 1. 解析数据源信息
	 *
	 * @param datasourceConfig 数据源配置
	 * @return 解析后 JDBC 数据源信息
	 */
	private ConnectionMetadata parseDatasource(DatasourceConfig datasourceConfig) {
		// 解析连接
		ConnectionMetadata metadata = new ConnectionMetadata(datasourceConfig.getUrl());
		log.info("已完成对配置数据库地址的解析！");
		log.info("数据库软件平台：{}", metadata.getDatabasePlatform());
		return metadata;
	}

	/**
	 * 2. 检查数据库是否存在，不存在则创建并执行数据库级别初始化SQL脚本
	 *
	 * @param coreConfig    核心配置
	 * @param metadata      连接元数据
	 * @param checkStrategy 数据库结构检查策略
	 */
	private void checkDatabase(CoreConfig coreConfig, ConnectionMetadata metadata, DatabaseStructureCheckStrategy checkStrategy) {
		// 判断是否需要检查数据库
		DatabaseCheckConfig databaseCheckConfig = coreConfig.getDatabaseCheck();
		if (databaseCheckConfig == null) {
			log.warn("未配置数据库检查项，跳过数据库检查！");
			return;
		}
		if (!databaseCheckConfig.isCheckDatabase()) {
			log.warn("已禁用数据库检查！将不会检查数据库是否存在，直接进行表格创建操作！");
			return;
		}
		// 检查数据库是否存在
		log.info("开始检查数据库是否需要初始化...");
		String checkUrl = checkStrategy.getCheckConnectionURL(metadata);
		if (StrUtil.isEmpty(checkUrl)) {
			throw new IllegalStateException("获取数据库检查连接地址失败！");
		}
		// 获取连接信息
		DatasourceConfig datasourceConfig = coreConfig.getDatasource();
		// 创建数据库
		try (Connection connection = DriverManager.getConnection(checkUrl, datasourceConfig.getUsername(), datasourceConfig.getPassword())) {
			if (!checkStrategy.databaseExists(metadata.getDatabaseName(), connection)) {
				// 创建数据库
				log.warn("数据库不存在！准备创建！");
				SQLExecuteUtils.createDatabase(metadata.getDatabasePlatform(), metadata.getDatabaseName(), connection);
				// 执行数据库级的初始化脚本
				log.info("开始执行数据库{}的库级初始化脚本...", metadata.getDatabaseName());
				if (CollectionUtil.isEmpty(databaseCheckConfig.getSqlPaths())) {
					log.warn("数据库级初始化脚本未配置，跳过数据库初始化！");
				} else {
					// 重连接至新建的数据库
					try (Connection newConnection = DriverManager.getConnection(metadata.toConnectionUrl(), datasourceConfig.getUsername(), datasourceConfig.getPassword())) {
						SQLExecuteUtils.batchRunSQLScript(databaseCheckConfig.getSqlPaths(), newConnection);
						log.info("已完成数据库级初始化脚本执行！");
					} catch (Exception e) {
						log.error("执行数据库级初始化脚本失败！", e);
						throw new RuntimeException(e);
					}
				}
			} else {
				log.info("数据库存在，不需要创建！");
			}
		} catch (Exception e) {
			log.error("连接至数据库检查元数据或执行数据库初始化脚本时失败！", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * 3. 按配置检查并初始化数据表
	 *
	 * @param coreConfig    核心配置
	 * @param metadata      连接元数据
	 * @param checkStrategy 数据库结构检查策略
	 */
	private void createTables(CoreConfig coreConfig, ConnectionMetadata metadata, DatabaseStructureCheckStrategy checkStrategy) {
		// 查看是否检查表
		TableCheckConfig tableCheckConfig = coreConfig.getTableCheck();
		if (tableCheckConfig == null) {
			log.warn("未配置表检查项，跳过表初始化！");
			return;
		}
		if (!tableCheckConfig.isCheckTable()) {
			log.warn("已禁用表检查！将不会检查或初始化数据表！");
			return;
		}
		List<TableSqlConfig> tableList = tableCheckConfig.getTableList();
		if (CollectionUtil.isEmpty(tableList)) {
			log.warn("未配置需要检查的数据表，跳过表初始化！");
			return;
		}
		// 获取连接信息
		DatasourceConfig datasourceConfig = coreConfig.getDatasource();
		// 根据表名配置创建数据库表
		try (Connection connection = DriverManager.getConnection(datasourceConfig.getUrl(), datasourceConfig.getUsername(), datasourceConfig.getPassword())) {
			connection.setAutoCommit(true);
			for (TableSqlConfig tableConfig : tableList) {
				if (StrUtil.isEmpty(tableConfig.getTableName())) {
					log.warn("存在未配置表名的表检查项，已跳过！");
					continue;
				}
				if (!checkStrategy.tableExists(metadata.getDatabaseName(), tableConfig.getSchemaName(), tableConfig.getTableName(), connection)) {
					log.warn("数据表{}不存在，将执行对应SQL脚本！", tableConfig.getTableName());
					SQLExecuteUtils.batchRunSQLScript(tableConfig.getSqlPaths(), connection);
					log.info("数据表{}初始化完成！", tableConfig.getTableName());
				} else {
					log.info("数据表{}已存在，不需要初始化！", tableConfig.getTableName());
				}
			}
		} catch (Exception e) {
			log.error("初始化表格失败！", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * 总体初始化全流程串联执行
	 *
	 * @param coreConfig 核心配置对象
	 */
	public final void runInitialize(CoreConfig coreConfig) {
		// 检查是否进行总体初始化步骤
		if (!coreConfig.isEnabled()) {
			log.warn("SQL自动初始化已禁用！将不会进行数据库检查和初始化操作！");
			return;
		}
		// 执行总体的流程
		DatabaseStructureCheckStrategy checkStrategy = getStructureCheckStrategy();
		ConnectionMetadata metadata = parseDatasource(coreConfig.getDatasource());
		checkDatabase(coreConfig, metadata, checkStrategy);
		createTables(coreConfig, metadata, checkStrategy);
	}

}