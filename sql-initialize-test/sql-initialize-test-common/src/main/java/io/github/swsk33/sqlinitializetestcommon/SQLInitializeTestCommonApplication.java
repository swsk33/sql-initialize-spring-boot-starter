package io.github.swsk33.sqlinitializetestcommon;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;

/**
 * 测试公用类加载起点
 */
@ComponentScan
@MapperScan("io.github.swsk33.sqlinitializetestcommon.dao")
public class SQLInitializeTestCommonApplication {
}