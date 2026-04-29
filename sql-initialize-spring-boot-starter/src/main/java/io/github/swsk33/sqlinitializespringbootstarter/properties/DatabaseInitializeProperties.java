package io.github.swsk33.sqlinitializespringbootstarter.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 配置信息
 */
@Data
@Component
@ConfigurationProperties(prefix = "io.github.swsk33.sql-init")
public class DatabaseInitializeProperties {

	/**
	 * 是否启用 SQL 初始化检查
	 */
	private boolean enabled = true;

	/**
	 * 数据库检查配置
	 */
	private DatabaseCheckProperties databaseCheck;

	/**
	 * 表检查配置
	 */
	private TableCheckProperties tableCheck;

	/**
	 * 数据库检查配置属性
	 */
	@Data
	public static class DatabaseCheckProperties {

		/**
		 * 是否检查数据库，不存在时会尝试创建
		 */
		private boolean checkDatabase = true;

		/**
		 * 数据库不存在并创建成功后执行的 SQL 脚本路径列表
		 */
		private List<String> sqlPaths;

	}

	/**
	 * 表检查配置属性
	 */
	@Data
	public static class TableCheckProperties {

		/**
		 * 是否检查表，不存在时会执行对应SQL脚本
		 */
		private boolean checkTable = true;

		/**
		 * 需要检查的表及对应 SQL 脚本列表
		 */
		private List<TableSqlProperties> tableList;

	}

	/**
	 * 单张表的检查及 SQL 脚本配置属性
	 */
	@Data
	public static class TableSqlProperties {

		/**
		 * 表所属schema，其中：
		 * <ul>
		 *     <li>PostgreSQL 默认使用 public</li>
		 *     <li>MySQL 默认为空</li>
		 * </ul>
		 */
		private String schemaName;

		/**
		 * 需要检查的表名
		 */
		private String tableName;

		/**
		 * 表不存在时执行的 SQL 脚本路径列表
		 */
		private List<String> sqlPaths;

	}

}