package com.task.Task_management.service;

import com.task.Task_management.dao.UserDAO;
import com.task.Task_management.exception.UserAlreadyExistsException;
import com.task.Task_management.exception.UserNotFoundException;
import com.task.Task_management.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.regex.Pattern;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserDAO userDAO;

    // Email validation pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$");

    public List<User> getAllUsers() {
        return userDAO.findAll();
    }

    public User getUserById(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("User Id must be positive");
        }

        User user = userDAO.findById(id);
        if (user == null) {
            throw new UserNotFoundException("User not found with id: " + id);
        }
        return user;
    }

    public User createUser(User user) {
        validateUser(user);

        if (isEmailExists(user.getEmail())) {
            throw new UserAlreadyExistsException("User already exists with email: " + user.getEmail());
        }

        int generatedId = userDAO.save(user);
        user.setId(generatedId);
        return user;
    }

    public User updateUser(User user) {
        validateUser(user);

        if (!userExists(user.getId())) {
            throw new UserNotFoundException("user not found with Id: " + user.getId());
        }

        User existingUser = userDAO.findById((user.getId()));
        if (!existingUser.getEmail().equals(user.getEmail()) && isEmailExists(user.getEmail())) {
            throw new UserAlreadyExistsException("Email already in use: " + user.getEmail());
        }

        userDAO.update(user);
        return user;
    }

    public boolean userExists(int id) {
        return userDAO.findById(id) != null;
    }

    public void deleteUser(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("User ID must be positive");
        }

        if (!userExists(id)) {
            throw new UserNotFoundException("User not found with ID: " + id);
        }

        userDAO.deleteById(id);
    }

    public User getUserByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }

        List<User> allUsers = userDAO.findAll();
        return allUsers.stream()
                .filter(user -> user.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
    }

    public List<User> getUsersByRole(String role) {
        validateRole(role);
        List<User> allUsers = userDAO.findAll();
        return allUsers.stream()
                .filter(user -> user.getRole().equalsIgnoreCase(role))
                .toList();
    }

    // helper method
    public void validateUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("user cannot be null");
        }

        validateUsername(user.getUsername());
        validateEmail(user.getEmail());
        validateRole(user.getRole());
    }

    public void validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("username cannot be null");
        }

        if (username.length() < 3) {
            throw new IllegalArgumentException("Username must be at least 3 characters long");
        }
        if (username.length() > 50) {
            throw new IllegalArgumentException("Username cannot exceed 50 characters");
        }
    }

    private void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Invalid email format");
        }
        if (email.length() > 100) {
            throw new IllegalArgumentException("Email cannot exceed 100 characters");
        }
    }

    private void validateRole(String role) {
        if (role == null || role.trim().isEmpty()) {
            throw new IllegalArgumentException("Role cannot be null or empty");
        }

        // Define allowed roles
        String[] validRoles = { "ADMIN", "USER", "MANAGER" };
        boolean isValidRole = false;

        for (String validRole : validRoles) {
            if (validRole.equalsIgnoreCase(role)) {
                isValidRole = true;
                break;
            }
        }
        if (!isValidRole) {
            throw new IllegalArgumentException("Invalid role. Must be: ADMIN, USER, or MANAGER");
        }
    }

    private boolean isEmailExists(String email) {
        List<User> allUsers = userDAO.findAll();
        return allUsers.stream()
                .anyMatch(user -> user.getEmail().equalsIgnoreCase(email));
    }

}
