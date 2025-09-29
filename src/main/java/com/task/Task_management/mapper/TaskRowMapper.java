package com.task.Task_management.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import org.springframework.jdbc.core.RowMapper;

import com.task.Task_management.model.Task;

public class TaskRowMapper implements RowMapper<Task> {
	
	@Override
	public Task mapRow(ResultSet rs, int rowNum) throws SQLException{
		Task task = new Task();
		
		task.setId(rs.getInt("id"));
		task.setName(rs.getString("name"));
		task.setDescription(rs.getString("description"));
		task.setStatus(rs.getString("status"));
		task.setPriority(rs.getInt("priority"));
		task.setDueDate(rs.getObject("dueDate", LocalDate.class));
		task.setProjectId(rs.getInt("projectId"));
		task.setUserId(rs.getInt("userId"));
		
		return task;
	}
	
}
