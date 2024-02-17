package io.github.swsk33.sqlinitializecore.util;


import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

/**
 * 获取数据库元数据相关的实用类
 */
@Slf4j
public class DatabaseMetadataUtils {

	/**
	 * 判断某个数据库是否存在
	 *
	 * @param databaseName 数据库名称
	 * @param connection   JDBC连接
	 * @return 对应名称数据库是否存在
	 */
	public static boolean databaseExists(String databaseName, Connection connection) {
		try {
			// 获取连接的元数据
			DatabaseMetaData metaData = connection.getMetaData();
			// 获取全部的Catalog（也就是数据库）元数据
			ResultSet catalogs = metaData.getCatalogs();
			// 遍历每个数据库名称
			while (catalogs.next()) {
				if (catalogs.getString("TABLE_CAT").equals(databaseName)) {
					return true;
				}
			}
		} catch (Exception e) {
			log.error("获取数据库相关元数据出错！");
			log.error(e.getMessage());
		}
		return false;
	}

	/**
	 * 获取一个数据库中的表格数量
	 *
	 * @param databaseName 数据库库名
	 * @param connection   数据库连接
	 * @return 表格数量
	 */
	public static int getDatabaseTableCount(String databaseName, Connection connection) {
		int result = 0;
		try {
			// 获取连接的元数据
			DatabaseMetaData metaData = connection.getMetaData();
			// 查询元数据中，对应的数据库中的所有的普通表格信息
			ResultSet tables = metaData.getTables(databaseName, null, null, new String[]{"TABLE"});
			// 遍历查询得到的表格结果
			while (tables.next()) {
				result++;
			}
		} catch (Exception e) {
			log.error("获取表格元数据出错！");
			log.error(e.getMessage());
			result = -1;
		}
		return result;
	}

}