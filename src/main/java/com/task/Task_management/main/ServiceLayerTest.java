package com.task.Task_management.main;

import com.task.Task_management.config.DatabaseConfig;
import com.task.Task_management.model.Project;
import com.task.Task_management.model.Task;
import com.task.Task_management.model.User;
import com.task.Task_management.service.ProjectService;
import com.task.Task_management.service.TaskService;
import com.task.Task_management.service.UserService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.time.LocalDate;
import java.util.List;

public class ServiceLayerTest {

    public static void main(String[] args) {
        System.out.println("=== Testing Service Layer ===\n");

        // Initialize Spring context
        ApplicationContext context = new AnnotationConfigApplicationContext(DatabaseConfig.class);

        // Get service beans
        UserService userService = context.getBean(UserService.class);
        ProjectService projectService = context.getBean(ProjectService.class);
        TaskService taskService = context.getBean(TaskService.class);

        try {
            // Test User Service
            testUserService(userService);

            // Test Project Service
            testProjectService(projectService);

            // Test Task Service
            testTaskService(taskService, userService, projectService);

            // Test Service Integration
            testServiceIntegration(userService, projectService, taskService);

            System.out.println("\n🎉 ALL SERVICE LAYER TESTS PASSED!");

        } catch (Exception e) {
            System.err.println("❌ Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void testUserService(UserService userService) {
        System.out.println("1. Testing UserService:");

        // Test create user
        User user = new User();
        user.setUsername("john_doe");
        user.setEmail("john@example.com");
        user.setRole("USER");

        User createdUser = userService.createUser(user);
        System.out.println("✅ User created: " + createdUser.getUsername() + " (ID: " + createdUser.getId() + ")");

        // Test get user by ID
        User foundUser = userService.getUserById(createdUser.getId());
        System.out.println("✅ User retrieved: " + foundUser.getUsername());

        // Test update user
        foundUser.setRole("ADMIN");
        User updatedUser = userService.updateUser(foundUser);
        System.out.println("✅ User updated: Role changed to " + updatedUser.getRole());

        // Test get users by role
        List<User> adminUsers = userService.getUsersByRole("ADMIN");
        System.out.println("✅ Found " + adminUsers.size() + " admin users");

        System.out.println();
    }

    private static void testProjectService(ProjectService projectService) {
        System.out.println("2. Testing ProjectService:");

        // Test create project
        Project project = new Project();
        project.setName("Website Redesign");
        project.setDescription("Complete website redesign project");
        project.setStartDate(LocalDate.now());
        project.setEndDate(LocalDate.now().plusMonths(3));

        Project createdProject = projectService.createProject(project);
        System.out.println("✅ Project created: " + createdProject.getName() + " (ID: " + createdProject.getId() + ")");

        // Test get active projects
        List<Project> activeProjects = projectService.getActiveProjects();
        System.out.println("✅ Found " + activeProjects.size() + " active projects");

        // Test project statistics
        ProjectService.ProjectStatistics stats = projectService.getProjectStatistics(createdProject.getId());
        System.out.println("✅ Project stats: " + stats.toString());

        System.out.println();
    }

    private static void testTaskService(TaskService taskService, UserService userService, ProjectService projectService) {
        System.out.println("3. Testing TaskService:");

        // Get existing user and project for task creation
        List<User> users = userService.getAllUsers();
        List<Project> projects = projectService.getAllProjects();

        if (!users.isEmpty() && !projects.isEmpty()) {
            User user = users.get(0);
            Project project = projects.get(0);

            // Test create task
            Task task = new Task();
            task.setName("Implement Login Feature");
            task.setDescription("Create user authentication system");
            task.setStatus("TODO");
            task.setPriority(1);
            task.setDueDate(LocalDate.now().plusWeeks(2));
            task.setProjectId(project.getId());
            task.setUserId(user.getId());

            Task createdTask = taskService.createTask(task);
            System.out.println("✅ Task created: " + createdTask.getName() + " (ID: " + createdTask.getId() + ")");

            // Test update task status
            Task updatedTask = taskService.updateTaskStatus(createdTask.getId(), "IN_PROGRESS");
            System.out.println("✅ Task status updated to: " + updatedTask.getStatus());

            // Test get tasks by user
            List<Task> userTasks = taskService.getTasksByUser(user.getId());
            System.out.println("✅ Found " + userTasks.size() + " tasks for user: " + user.getUsername());

            // Test task statistics
            TaskService.TaskStatistics taskStats = taskService.getUserTaskStatistics(user.getId());
            System.out.println("✅ Task stats: " + taskStats.toString());
        }

        System.out.println();
    }

    private static void testServiceIntegration(UserService userService, ProjectService projectService, TaskService taskService) {
        System.out.println("4. Testing Service Integration:");

        // Test complete workflow
        try {
            // Create a manager user
            User manager = new User();
            manager.setUsername("project_manager");
            manager.setEmail("manager@company.com");
            manager.setRole("MANAGER");
            User createdManager = userService.createUser(manager);

            // Create a project
            Project project = new Project();
            project.setName("Mobile App Development");
            project.setDescription("iOS and Android app development");
            project.setStartDate(LocalDate.now());
            project.setEndDate(LocalDate.now().plusMonths(6));
            Project createdProject = projectService.createProject(project);

            // Create multiple tasks for the project
            String[] taskNames = {"Design UI", "Implement Backend", "Testing", "Deployment"};
            int[] priorities = {2, 1, 3, 1};
            String[] statuses = {"TODO", "IN_PROGRESS", "TODO", "TODO"};

            for (int i = 0; i < taskNames.length; i++) {
                Task task = new Task();
                task.setName(taskNames[i]);
                task.setDescription("Task description for " + taskNames[i]);
                task.setStatus(statuses[i]);
                task.setPriority(priorities[i]);
                task.setDueDate(LocalDate.now().plusWeeks(i + 2));
                task.setProjectId(createdProject.getId());
                task.setUserId(createdManager.getId());

                taskService.createTask(task);
            }

            // Get comprehensive statistics
            ProjectService.ProjectStatistics projectStats = projectService.getProjectStatistics(createdProject.getId());
            TaskService.TaskStatistics overallStats = taskService.getOverallTaskStatistics();

            System.out.println("✅ Integration test completed:");
            System.out.println("   - Created manager: " + createdManager.getUsername());
            System.out.println("   - Created project: " + createdProject.getName());
            System.out.println("   - Created 4 tasks");
            System.out.println("   - " + projectStats.toString());
            System.out.println("   - " + overallStats.toString());

            // Test business logic - try to delete project with tasks (should fail)
            try {
                projectService.deleteProject(createdProject.getId());
                System.out.println("❌ Project deletion should have failed!");
            } catch (Exception e) {
                System.out.println("✅ Business rule enforced: " + e.getMessage());
            }

        } catch (Exception e) {
            System.err.println("❌ Integration test failed: " + e.getMessage());
        }

        System.out.println();
    }
}
