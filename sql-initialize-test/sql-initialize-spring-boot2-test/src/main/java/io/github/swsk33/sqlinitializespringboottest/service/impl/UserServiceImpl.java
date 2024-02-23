package io.github.swsk33.sqlinitializespringboottest.service.impl;

import io.github.swsk33.sqlinitializespringboottest.dao.UserDAO;
import io.github.swsk33.sqlinitializespringboottest.dataobject.User;
import io.github.swsk33.sqlinitializespringboottest.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Slf4j
@Component
@DependsOn("SQLInitializeAutoConfigure")
public class UserServiceImpl implements UserService {

	@Autowired
	private UserDAO userDAO;

	@PostConstruct
	private void init() {
		log.info("执行数据库测试访问...");
		List<User> users = userDAO.getAll();
		for (User user : users) {
			System.out.println(user);
		}
	}

}