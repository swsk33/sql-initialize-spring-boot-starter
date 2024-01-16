package io.github.swsk33.sqlinitializespringbootstarter.autoconfigure;

import io.github.swsk33.sqlinitializecore.model.ConnectionMetadata;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.Configuration;

/**
 * 读取并解析原始的数据源配置
 */
@Data
@Slf4j
@Configuration
@AutoConfigureBefore(DatabaseInitializeAutoConfigure.class)
public class OriginDatasourceAutoConfigure {

	/**
	 * 读取连接地址
	 */
	@Value("${spring.datasource.url}")
	private String url;

	/**
	 * 读取用户名
	 */
	@Value("${spring.datasource.username}")
	private String username;

	/**
	 * 读取密码
	 */
	@Value("${spring.datasource.password}")
	private String password;

	/**
	 * 解析后的连接信息
	 */
	private ConnectionMetadata metadata;

	/**
	 * 解析原始配置地址中的数据库地址、数据库名等等信息
	 */
	@PostConstruct
	private void parseOriginURL() {
		log.info("------- SQL自动初始化开始自动配置φ(>ω<*)  -------");
		// 解析URL
		metadata = new ConnectionMetadata(url);
		log.info("已完成对配置数据库地址的解析！");
		log.info("数据库软件平台：{}", metadata.getDatabasePlatform());
	}

}