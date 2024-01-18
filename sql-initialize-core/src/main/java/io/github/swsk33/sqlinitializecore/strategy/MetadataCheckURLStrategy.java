package io.github.swsk33.sqlinitializecore.strategy;

import io.github.swsk33.sqlinitializecore.model.ConnectionMetadata;

/**
 * 在第一次重组URL并连接数据库检测元数据时，生成检测元数据的连接地址策略
 * 例如MySQL可以不指定任何数据库直接连接，但是PostgreSQL则不行，因此此时如果是PostgreSQL则会连接上一个默认内置数据库检测元数据
 */
public interface MetadataCheckURLStrategy {

	/**
	 * 根据不同数据库平台，获取检测元数据的连接地址
	 *
	 * @param urlMeta 已解析的地址信息
	 * @return 检测时连接地址
	 */
	String getCheckConnectionURL(ConnectionMetadata urlMeta);

}