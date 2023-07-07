package io.github.swsk33.sqlinitializespringbootstarter.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.Configuration;

import java.net.URI;
import java.net.URISyntaxException;

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
	 * 数据库的软件平台（例如MySQL）
	 */
	private String databasePlatform;

	/**
	 * 数据库连接的地址和端口
	 */
	private String hostAndPort;

	/**
	 * 指定连接的数据库名
	 */
	private String databaseName;

	/**
	 * 解析原始配置地址中的数据库地址、数据库名等等信息
	 */
	@PostConstruct
	private void parseOriginURL() {
		log.info("------- SQL自动初始化开始自动配置φ(>ω<*)  -------");
		try {
			// 解析URL
			URI origin = new URI(url.replace("jdbc:", ""));
			// 获取数据库平台
			this.databasePlatform = origin.getScheme().toLowerCase();
			// 获取数据库地址端口
			this.hostAndPort = origin.getAuthority();
			// 获取数据库名
			this.databaseName = origin.getPath().substring(1);
			log.info("已完成对配置数据库地址的解析！数据库软件平台：" + databasePlatform);
		} catch (URISyntaxException e) {
			log.error("解析数据库连接地址错误！请检查连接地址配置！");
			throw new RuntimeException(e);
		}
	}

}