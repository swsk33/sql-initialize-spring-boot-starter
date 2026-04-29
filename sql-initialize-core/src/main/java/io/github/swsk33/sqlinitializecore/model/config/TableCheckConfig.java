package io.github.swsk33.sqlinitializecore.model.config;

import lombok.Builder;
import lombok.Value;

import java.util.List;

/**
 * 表存在性检查及初始化配置。
 */
@Value
@Builder
public class TableCheckConfig {

	/**
	 * 是否检查表，不存在时会执行对应SQL脚本。
	 */
	@Builder.Default
	boolean checkTable = true;

	/**
	 * 需要检查的表及对应SQL脚本列表。
	 */
	List<TableSqlConfig> tableList;

}