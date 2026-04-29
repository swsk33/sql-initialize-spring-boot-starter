package io.github.swsk33.sqlinitializetestcommon.service;

import io.github.swsk33.sqlinitializetestcommon.dao.UserDAO;
import io.github.swsk33.sqlinitializetestcommon.dataobject.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@DependsOn("SQLInitializeAutoConfigure")
public class UserService implements InitializingBean {

	@Autowired
	private UserDAO userDAO;

	public void afterPropertiesSet() {
		log.info("执行数据库测试访问...");
		List<User> users = userDAO.getAll();
		for (User user : users) {
			System.out.println(user);
		}
	}

}