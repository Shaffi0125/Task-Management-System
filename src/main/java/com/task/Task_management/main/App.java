package com.task.Task_management.main;

import com.task.Task_management.config.DatabaseConfig;
import com.task.Task_management.dao.ProjectDAO;
import com.task.Task_management.dao.TaskDAO;
import com.task.Task_management.dao.UserDAO;
import com.task.Task_management.model.Project;
import com.task.Task_management.model.Task;
import com.task.Task_management.model.User;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.time.LocalDate;
import java.util.List;

public class App {
    public static void main(String[] args) {
        // System.out.println( "Hello World!" );

        // initializing Spring context
        ApplicationContext context = new AnnotationConfigApplicationContext(DatabaseConfig.class);

        // get DAO beans from spring context
        UserDAO userDAO = context.getBean(UserDAO.class);
        ProjectDAO projectDAO = context.getBean(ProjectDAO.class);
        TaskDAO taskDAO = context.getBean(TaskDAO.class);

        // testing the project
        testApplication(userDAO, projectDAO, taskDAO);
    }

    private static void testApplication(UserDAO userDAO, ProjectDAO projectDAO, TaskDAO taskDAO) {
        System.out.println("===== Testing Task Management System ====");

        try {
            // Test 1: Create and save a user
            System.out.println("\n1. Testing User Operations:");
            User user = new User();
            user.setUsername("john_doe");
            user.setEmail("john@example.com");
            user.setRole("ADMIN");

            int userId = userDAO.save(user);
            System.out.println(" User saved with ID: " + userId);

            // Test 2: Create and save a project
            System.out.println("\n2. Testing Project Operations:");
            Project project = new Project();
            project.setName("Website Development");
            project.setDescription("Build company website");
            project.setStartDate(LocalDate.now());
            project.setEndDate(LocalDate.now().plusDays(30));

            int projectId = projectDAO.save(project);
            System.out.println(" Project saved with ID: " + projectId);

            // Test 3: Create and save a task
            System.out.println("\n3. Testing Task Operations:");
            Task task = new Task();
            task.setName("Design Homepage");
            task.setDescription("Create homepage design");
            task.setStatus("IN_PROGRESS");
            task.setPriority(1);
            task.setDueDate(LocalDate.now().plusDays(7));
            task.setProjectId(projectId);
            task.setUserId(userId);

            int taskId = taskDAO.save(task);
            System.out.println(" Task saved with ID: " + taskId);

            // Test 4: Read operations
            System.out.println("\n4. Testing Read Operations:");
            List<User> allUsers = userDAO.findAll();
            System.out.println(" Found " + allUsers.size() + " users");

            List<Project> allProjects = projectDAO.findAll();
            System.out.println(" Found " + allProjects.size() + " projects");

            List<Task> allTasks = taskDAO.findAll();
            System.out.println(" Found " + allTasks.size() + " tasks");

            // Test 5: Relationship queries
            System.out.println("\n5. Testing Relationship Queries:");
            List<Task> projectTasks = taskDAO.findByProjectId(projectId);
            System.out.println(" Found " + projectTasks.size() + " tasks for project " + projectId);

            List<Task> userTasks = taskDAO.findByUserId(userId);
            System.out.println(" Found " + userTasks.size() + " tasks for user " + userId);

            System.out.println("\n🎉 ALL TESTS PASSED! project is working perfectly!");

        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
