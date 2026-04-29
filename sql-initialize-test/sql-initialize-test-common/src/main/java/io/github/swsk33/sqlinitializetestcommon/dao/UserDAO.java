package io.github.swsk33.sqlinitializetestcommon.dao;

import io.github.swsk33.sqlinitializetestcommon.dataobject.User;

import java.util.List;

public interface UserDAO {

	List<User> getAll();

}