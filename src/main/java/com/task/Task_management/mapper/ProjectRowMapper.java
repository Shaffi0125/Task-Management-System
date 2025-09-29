package com.task.Task_management.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import org.springframework.jdbc.core.RowMapper;
import com.task.Task_management.model.Project;

public class ProjectRowMapper implements RowMapper<Project> {
    
    @Override
    public Project mapRow(ResultSet rs, int rowNum) throws SQLException {
        Project project = new Project();
        
        // Map all Project fields from database columns
        project.setId(rs.getInt("id"));
        project.setName(rs.getString("name"));
        project.setDescription(rs.getString("description"));
        project.setStartDate(rs.getObject("startDate", LocalDate.class));
        project.setEndDate(rs.getObject("endDate", LocalDate.class));
        
        return project;
    }
}
