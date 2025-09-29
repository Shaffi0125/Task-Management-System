package com.task.Task_management.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.task.Task_management.model.User;

public class UserRowMapper implements RowMapper<User> {
	
	@Override
	public User mapRow(ResultSet rs, int rowNum) throws SQLException{
		User user = new User();
		
		user.setId(rs.getInt("id"));
		user.setUsername(rs.getString("username"));
		user.setEmail(rs.getString("email"));
		user.setRole(rs.getString("role"));
		
		return user;
	}
	
}
