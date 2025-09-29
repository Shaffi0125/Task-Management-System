package com.task.Task_management.dao;

import com.task.Task_management.mapper.TaskRowMapper;
import com.task.Task_management.model.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.List;

@Repository
public class TaskDAO {

    @Autowired
    private JdbcTemplate jdbc;

    private TaskRowMapper mapper = new TaskRowMapper();

    public List<Task> findAll() {
        String sql = "select * from tasks";
        return jdbc.query(sql, mapper);
    }

    public Task findById(int id) {
        String sql = "select * from tasks where id=?";
        List<Task> tasks = jdbc.query(sql, mapper, id);
        if (tasks.isEmpty()) {
            return null;
        } else {
            return tasks.get(0);
        }
    }

    public int save(Task task) {
        String sql = "insert into tasks (name, description, status, priority, dueDate, projectId, userId) values(?, ?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[] { "id" });
            ps.setString(1, task.getName());
            ps.setString(2, task.getDescription());
            ps.setString(3, task.getStatus());
            ps.setInt(4, task.getPriority());
            ps.setDate(5, Date.valueOf(task.getDueDate()));
            ps.setInt(6, task.getProjectId());
            ps.setInt(7, task.getUserId());
            return ps;
        }, keyHolder);

        return keyHolder.getKey().intValue();
    }

    public void update(Task task) {
        String sql = "update tasks set name=?, description=?, status=?, priority=?, dueDate=?, projectId=?, userId=? where id=?";
        jdbc.update(sql, task.getName(), task.getDescription(), task.getStatus(), task.getPriority(), task.getDueDate(),
                task.getProjectId(), task.getUserId(), task.getId());
    }

    public void deleteById(int id) {
        String sql = "delete from tasks where id=?";
        jdbc.update(sql, id);
    }

    public List<Task> findByProjectId(int projectId) {
        String sql = "select * from tasks where projectId=?";
        return jdbc.query(sql, mapper, projectId);
    }

    public List<Task> findByUserId(int userId) {
        String sql = "select * from tasks where userId=?";
        return jdbc.query(sql, mapper, userId);
    }

    public List<Task> findByStatus(String status) {
        String sql = "select * from tasks where status=?";
        return jdbc.query(sql, mapper, status);
    }

    public List<Task> findByPriorityDesc() {
        String sql = "select * from tasks order by priority desc";
        return jdbc.query(sql, mapper);
    }

    public int countTasksByProject(int projectId) {
        String sql = "select count(*) from tasks where projectId=?";
        Integer count = jdbc.queryForObject(sql, Integer.class, projectId);
        return count != null ? count : 0;
    }

    public List<Task> findOverdueTasks() {
        String sql = "select * from tasks where dueDate < current_date";
        return jdbc.query(sql, mapper);
    }
}
