package io.github.swsk33.sqlinitializecore.model.config;

import lombok.Builder;
import lombok.Value;

import java.util.List;

/**
 * 数据库存在性检查及初始化配置。
 */
@Value
@Builder
public class DatabaseCheckConfig {

	/**
	 * 是否检查数据库，不存在时会尝试创建。
	 */
	@Builder.Default
	boolean checkDatabase = true;

	/**
	 * 数据库不存在并创建成功后执行的SQL脚本路径列表。
	 */
	List<String> sqlPaths;

}