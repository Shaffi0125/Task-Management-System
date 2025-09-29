package com.task.Task_management.service;

import com.task.Task_management.dao.TaskDAO;
import com.task.Task_management.dao.UserDAO;
import com.task.Task_management.dao.ProjectDAO;
import com.task.Task_management.exception.InvalidTaskException;
import com.task.Task_management.exception.TaskNotFoundException;
import com.task.Task_management.model.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TaskService {

    @Autowired
    private TaskDAO taskDAO;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private ProjectDAO projectDAO;

    // Method 1: Get all tasks
    public List<Task> getAllTasks() {
        return taskDAO.findAll();
    }

    // Method 2: Get task by ID with validation
    public Task getTaskById(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Task ID must be positive");
        }

        Task task = taskDAO.findById(id);
        if (task == null) {
            throw new TaskNotFoundException(id);
        }
        return task;
    }

    // Method 3: Create new task with validation
    public Task createTask(Task task) {
        // Validate input
        validateTask(task);

        // Validate foreign key relationships
        validateTaskRelationships(task);

        // Save task and return with generated ID
        int generatedId = taskDAO.save(task);
        task.setId(generatedId);
        return task;
    }

    // Method 4: Update existing task
    public Task updateTask(Task task) {
        // Validate input
        validateTask(task);

        // Check if task exists
        if (!taskExists(task.getId())) {
            throw new TaskNotFoundException(task.getId());
        }

        // Validate foreign key relationships
        validateTaskRelationships(task);

        taskDAO.update(task);
        return task;
    }

    // Method 5: Delete task by ID
    public void deleteTask(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Task ID must be positive");
        }

        if (!taskExists(id)) {
            throw new TaskNotFoundException(id);
        }

        taskDAO.deleteById(id);
    }

    // Method 6: Get tasks by project ID
    public List<Task> getTasksByProject(int projectId) {
        if (projectId <= 0) {
            throw new IllegalArgumentException("Project ID must be positive");
        }

        // Validate project exists
        if (projectDAO.findById(projectId) == null) {
            throw new IllegalArgumentException("Project not found with ID: " + projectId);
        }

        return taskDAO.findByProjectId(projectId);
    }

    // Method 7: Get tasks by user ID
    public List<Task> getTasksByUser(int userId) {
        if (userId <= 0) {
            throw new IllegalArgumentException("User ID must be positive");
        }

        // Validate user exists
        if (userDAO.findById(userId) == null) {
            throw new IllegalArgumentException("User not found with ID: " + userId);
        }

        return taskDAO.findByUserId(userId);
    }

    // Method 8: Get tasks by status
    public List<Task> getTasksByStatus(String status) {
        validateTaskStatus(status);
        return taskDAO.findByStatus(status);
    }

    // Method 9: Get overdue tasks
    public List<Task> getOverdueTasks() {
        return taskDAO.findOverdueTasks();
    }

    // Method 10: Get tasks by priority
    public List<Task> getTasksByPriority(int priority) {
        validateTaskPriority(priority);

        List<Task> allTasks = taskDAO.findAll();
        return allTasks.stream()
                .filter(task -> task.getPriority() == priority)
                .collect(Collectors.toList());
    }

    // Method 11: Get tasks due within specific days
    public List<Task> getTasksDueWithin(int days) {
        if (days < 0) {
            throw new IllegalArgumentException("Days must be non-negative");
        }

        LocalDate cutoffDate = LocalDate.now().plusDays(days);
        List<Task> allTasks = taskDAO.findAll();

        return allTasks.stream()
                .filter(task -> !task.getDueDate().isAfter(cutoffDate) &&
                        !task.getDueDate().isBefore(LocalDate.now()))
                .collect(Collectors.toList());
    }

    // Method 12: Update task status
    public Task updateTaskStatus(int taskId, String status) {
        validateTaskStatus(status);

        Task task = getTaskById(taskId); // This validates existence
        task.setStatus(status);

        taskDAO.update(task);
        return task;
    }

    // Method 13: Update task priority
    public Task updateTaskPriority(int taskId, int priority) {
        validateTaskPriority(priority);

        Task task = getTaskById(taskId); // This validates existence
        task.setPriority(priority);

        taskDAO.update(task);
        return task;
    }

    // Method 14: Assign task to different user
    public Task reassignTask(int taskId, int newUserId) {
        if (newUserId <= 0) {
            throw new IllegalArgumentException("User ID must be positive");
        }

        // Validate user exists
        if (userDAO.findById(newUserId) == null) {
            throw new IllegalArgumentException("User not found with ID: " + newUserId);
        }

        Task task = getTaskById(taskId); // This validates task existence
        task.setUserId(newUserId);

        taskDAO.update(task);
        return task;
    }

    // Method 15: Get task statistics for a project
    public TaskStatistics getProjectTaskStatistics(int projectId) {
        if (projectId <= 0) {
            throw new IllegalArgumentException("Project ID must be positive");
        }

        List<Task> projectTasks = getTasksByProject(projectId);
        return calculateTaskStatistics(projectTasks, "Project " + projectId);
    }

    // Method 16: Get task statistics for a user
    public TaskStatistics getUserTaskStatistics(int userId) {
        if (userId <= 0) {
            throw new IllegalArgumentException("User ID must be positive");
        }

        List<Task> userTasks = getTasksByUser(userId);
        return calculateTaskStatistics(userTasks, "User " + userId);
    }

    // Method 17: Get overall task statistics
    public TaskStatistics getOverallTaskStatistics() {
        List<Task> allTasks = taskDAO.findAll();
        return calculateTaskStatistics(allTasks, "Overall");
    }

    // Method 18: Check if task exists
    public boolean taskExists(int id) {
        return taskDAO.findById(id) != null;
    }

    // Private validation methods

    private void validateTask(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null");
        }

        validateTaskName(task.getName());
        validateTaskDescription(task.getDescription());
        validateTaskStatus(task.getStatus());
        validateTaskPriority(task.getPriority());
        validateTaskDueDate(task.getDueDate());
    }

    private void validateTaskName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Task name cannot be null or empty");
        }
        if (name.length() < 2) {
            throw new IllegalArgumentException("Task name must be at least 2 characters long");
        }
        if (name.length() > 50) {
            throw new IllegalArgumentException("Task name cannot exceed 50 characters");
        }
    }

    private void validateTaskDescription(String description) {
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Task description cannot be null or empty");
        }
        if (description.length() > 100) {
            throw new IllegalArgumentException("Task description cannot exceed 100 characters");
        }
    }

    private void validateTaskStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Task status cannot be null or empty");
        }

        String[] validStatuses = { "TODO", "IN_PROGRESS", "COMPLETED", "BLOCKED" };
        boolean isValidStatus = false;

        for (String validStatus : validStatuses) {
            if (validStatus.equalsIgnoreCase(status)) {
                isValidStatus = true;
                break;
            }
        }

        if (!isValidStatus) {
            throw new IllegalArgumentException(
                    "Invalid task status. Must be: TODO, IN_PROGRESS, COMPLETED, or BLOCKED");
        }
    }

    private void validateTaskPriority(int priority) {
        if (priority < 1 || priority > 5) {
            throw new IllegalArgumentException("Task priority must be between 1 and 5");
        }
    }

    private void validateTaskDueDate(LocalDate dueDate) {
        if (dueDate == null) {
            throw new IllegalArgumentException("Task due date cannot be null");
        }

        // Allow past due dates (for importing historical data)
        // But warn if too far in the future
        LocalDate oneYearFromNow = LocalDate.now().plusYears(1);
        if (dueDate.isAfter(oneYearFromNow)) {
            throw new InvalidTaskException("Task due date cannot be more than one year in the future");
        }
    }

    private void validateTaskRelationships(Task task) {
        // Validate project exists
        if (projectDAO.findById(task.getProjectId()) == null) {
            throw new InvalidTaskException("Project not found with ID: " + task.getProjectId());
        }

        // Validate user exists
        if (userDAO.findById(task.getUserId()) == null) {
            throw new InvalidTaskException("User not found with ID: " + task.getUserId());
        }
    }

    private TaskStatistics calculateTaskStatistics(List<Task> tasks, String context) {
        long totalTasks = tasks.size();

        long todoTasks = tasks.stream()
                .filter(task -> "TODO".equalsIgnoreCase(task.getStatus()))
                .count();

        long inProgressTasks = tasks.stream()
                .filter(task -> "IN_PROGRESS".equalsIgnoreCase(task.getStatus()))
                .count();

        long completedTasks = tasks.stream()
                .filter(task -> "COMPLETED".equalsIgnoreCase(task.getStatus()))
                .count();

        long blockedTasks = tasks.stream()
                .filter(task -> "BLOCKED".equalsIgnoreCase(task.getStatus()))
                .count();

        long overdueTasks = tasks.stream()
                .filter(task -> task.getDueDate().isBefore(LocalDate.now()) &&
                        !"COMPLETED".equalsIgnoreCase(task.getStatus()))
                .count();

        double completionPercentage = totalTasks > 0 ? (completedTasks * 100.0 / totalTasks) : 0.0;

        return new TaskStatistics(context, totalTasks, todoTasks, inProgressTasks,
                completedTasks, blockedTasks, overdueTasks, completionPercentage);
    }

    // Inner class for task statistics
    public static class TaskStatistics {
        private final String context;
        private final long totalTasks;
        private final long todoTasks;
        private final long inProgressTasks;
        private final long completedTasks;
        private final long blockedTasks;
        private final long overdueTasks;
        private final double completionPercentage;

        public TaskStatistics(String context, long totalTasks, long todoTasks,
                long inProgressTasks, long completedTasks, long blockedTasks,
                long overdueTasks, double completionPercentage) {
            this.context = context;
            this.totalTasks = totalTasks;
            this.todoTasks = todoTasks;
            this.inProgressTasks = inProgressTasks;
            this.completedTasks = completedTasks;
            this.blockedTasks = blockedTasks;
            this.overdueTasks = overdueTasks;
            this.completionPercentage = completionPercentage;
        }

        // Getters
        public String getContext() {
            return context;
        }

        public long getTotalTasks() {
            return totalTasks;
        }

        public long getTodoTasks() {
            return todoTasks;
        }

        public long getInProgressTasks() {
            return inProgressTasks;
        }

        public long getCompletedTasks() {
            return completedTasks;
        }

        public long getBlockedTasks() {
            return blockedTasks;
        }

        public long getOverdueTasks() {
            return overdueTasks;
        }

        public double getCompletionPercentage() {
            return completionPercentage;
        }

        @Override
        public String toString() {
            return String.format("%s Statistics | Total: %d | TODO: %d | In Progress: %d | " +
                    "Completed: %d | Blocked: %d | Overdue: %d | Progress: %.1f%%",
                    context, totalTasks, todoTasks, inProgressTasks, completedTasks,
                    blockedTasks, overdueTasks, completionPercentage);
        }
    }
}
