package io.github.swsk33.sqlinitializespringboot2test;

import io.github.swsk33.sqlinitializetestcommon.SQLInitializeTestCommonApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(SQLInitializeTestCommonApplication.class)
public class SqlInitializeSpringBootTestApplication {

	public static void main(String[] args) {
		SpringApplication.run(SqlInitializeSpringBootTestApplication.class, args);
	}

}