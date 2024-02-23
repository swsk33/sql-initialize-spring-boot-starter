package io.github.swsk33.sqlinitializespringboottest.dao;

import io.github.swsk33.sqlinitializespringboottest.dataobject.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserDAO {

	List<User> getAll();

}