package com.task.Task_management.dao;

import com.task.Task_management.mapper.ProjectRowMapper;
import com.task.Task_management.model.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.List;

@Repository
public class ProjectDAO {

    @Autowired
    private JdbcTemplate jdbc;

    private ProjectRowMapper mapper = new ProjectRowMapper();

    public List<Project> findAll() {
        String sql = "select * from projects";
        return jdbc.query(sql, mapper);
    }

    public Project findById(int id) {
        String sql = "select * from projects where id = ?";
        List<Project> projects = jdbc.query(sql, mapper, id);
        if (projects.isEmpty()) {
            return null;
        } else {
            return projects.get(0);
        }
    }

    public int save(Project project) {
        String sql = "insert into projects (name, description, startDate, endDate) values(?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[] { "id" });
            ps.setString(1, project.getName());
            ps.setString(2, project.getDescription());
            ps.setDate(3, Date.valueOf(project.getStartDate()));
            ps.setDate(4, Date.valueOf(project.getEndDate()));
            return ps;
        }, keyHolder);

        return keyHolder.getKey().intValue();
    }

    public void update(Project project) {
        String sql = "update projects set name=?, description=?, startDate=?, endDate=? where id=?";
        jdbc.update(sql, project.getName(), project.getDescription(), project.getStartDate(), project.getEndDate(),
                project.getId());
    }

    public void deleteById(int id) {
        String sql = "select * from projects where id=?";
        jdbc.update(sql, id);
    }

    public List<Project> findActiveProjects() {
        String sql = "select * from projects where endDate is null";
        return jdbc.query(sql, mapper);
    }

    public List<Project> findProjectsByDateRange(LocalDate startDate, LocalDate endDate) {
        String sql = "select * from projects where startDate>=? and (endDate<= or endDate is null)?";
        return jdbc.query(sql, mapper, Date.valueOf(startDate), Date.valueOf(endDate));
    }

    public int countProjects() {
        String sql = "select count(*) from projects";
        return jdbc.queryForObject(sql, Integer.class);
    }
}
