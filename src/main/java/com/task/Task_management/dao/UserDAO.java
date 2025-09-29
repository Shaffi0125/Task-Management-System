package com.task.Task_management.dao;

import com.task.Task_management.mapper.UserRowMapper;
import com.task.Task_management.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;

@Repository
public class UserDAO {

    private UserRowMapper mapper = new UserRowMapper();
    private JdbcTemplate jdbc;

    public JdbcTemplate getJdbc() {
        return jdbc;
    }

    @Autowired
    public void setJdbc(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<User> findAll() {
        String sql = "select * from users";
        return jdbc.query(sql, mapper);
    }

    public User findById(int id) {
        String sql = "select * from users where id = ?";
        List<User> users = jdbc.query(sql, mapper, id);
        if (users.isEmpty()) {
            return null; // No user found
        }
        return users.get(0); // Return first (and only) user
    }

    public int save(User user) {
        String sql = "insert into users (username, email, role) values(?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[] { "id" });
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getRole());
            return ps;
        }, keyHolder);

        return keyHolder.getKey().intValue();
    }

    public void update(User user) {
        String sql = "update users set username = ?, email = ?, role = ? where id = ?";
        jdbc.update(sql, user.getUsername(), user.getEmail(), user.getRole(), user.getId());
    }

    public void deleteById(int id) {
        String sql = "delete from users where id = ?";
        jdbc.update(sql, id);
    }

}
