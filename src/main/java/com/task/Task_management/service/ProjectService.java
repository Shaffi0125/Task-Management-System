package com.task.Task_management.service;

import com.task.Task_management.dao.ProjectDAO;
import com.task.Task_management.dao.TaskDAO;
import com.task.Task_management.exception.InvalidProjectException;
import com.task.Task_management.exception.ProjectNotFoundException;
import com.task.Task_management.model.Project;
import com.task.Task_management.model.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class ProjectService {

    @Autowired
    private ProjectDAO projectDAO;

    @Autowired
    private TaskDAO taskDAO;

    // Method 1: Get all projects
    public List<Project> getAllProjects() {
        return projectDAO.findAll();
    }

    // Method 2: Get project by ID with validation
    public Project getProjectById(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Project ID must be positive");
        }

        Project project = projectDAO.findById(id);
        if (project == null) {
            throw new ProjectNotFoundException(id);
        }
        return project;
    }

    // Method 3: Create new project with validation
    public Project createProject(Project project) {
        // Validate input
        validateProject(project);

        // Save project and return with generated ID
        int generatedId = projectDAO.save(project);
        project.setId(generatedId);
        return project;
    }

    // Method 4: Update existing project
    public Project updateProject(Project project) {
        // Validate input
        validateProject(project);

        // Check if project exists
        if (!projectExists(project.getId())) {
            throw new ProjectNotFoundException(project.getId());
        }

        projectDAO.update(project);
        return project;
    }

    // Method 5: Delete project by ID
    public void deleteProject(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Project ID must be positive");
        }

        if (!projectExists(id)) {
            throw new ProjectNotFoundException(id);
        }

        // Check if project has tasks
        List<Task> projectTasks = taskDAO.findByProjectId(id);
        if (!projectTasks.isEmpty()) {
            String errorMessage = "Cannot delete project with existing tasks. " +
                    "Please delete all tasks first. Found " +
                    projectTasks.size() + " tasks.";
            throw new InvalidProjectException(errorMessage);
        }

        projectDAO.deleteById(id);
    }


    // Method 6: Get active projects (no end date or end date in future)
    public List<Project> getActiveProjects() {
        return projectDAO.findActiveProjects();
    }

    // Method 7: Get projects by date range
    public List<Project> getProjectsByDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Start date and end date cannot be null");
        }

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }

        List<Project> allProjects = projectDAO.findAll();
        return allProjects.stream()
                .filter(project -> {
                    LocalDate projectStart = project.getStartDate();
                    LocalDate projectEnd = project.getEndDate();

                    // Project overlaps with date range
                    return projectStart.isBefore(endDate.plusDays(1)) &&
                            (projectEnd == null || projectEnd.isAfter(startDate.minusDays(1)));
                })
                .toList();
    }

    // Method 8: Get overdue projects
    public List<Project> getOverdueProjects() {
        LocalDate today = LocalDate.now();
        List<Project> allProjects = projectDAO.findAll();

        return allProjects.stream()
                .filter(project -> project.getEndDate() != null &&
                        project.getEndDate().isBefore(today))
                .toList();
    }

    // Method 9: Get project statistics
    public ProjectStatistics getProjectStatistics(int projectId) {
        Project project = getProjectById(projectId); // This validates existence

        List<Task> projectTasks = taskDAO.findByProjectId(projectId);

        long totalTasks = projectTasks.size();
        long completedTasks = projectTasks.stream()
                .filter(task -> "COMPLETED".equalsIgnoreCase(task.getStatus()))
                .count();
        long pendingTasks = totalTasks - completedTasks;

        return new ProjectStatistics(
                project.getId(),
                project.getName(),
                totalTasks,
                completedTasks,
                pendingTasks,
                totalTasks > 0 ? (completedTasks * 100.0 / totalTasks) : 0.0
        );
    }

    // Method 10: Check if project exists
    public boolean projectExists(int id) {
        return projectDAO.findById(id) != null;
    }

    // Method 11: Count total projects
    public int getTotalProjectCount() {
        return projectDAO.countProjects();
    }

    // Method 12: Search projects by name
    public List<Project> searchProjectsByName(String namePattern) {
        if (namePattern == null || namePattern.trim().isEmpty()) {
            throw new IllegalArgumentException("Search pattern cannot be null or empty");
        }

        List<Project> allProjects = projectDAO.findAll();
        return allProjects.stream()
                .filter(project -> project.getName().toLowerCase()
                        .contains(namePattern.toLowerCase()))
                .toList();
    }

    // Private validation methods

    private void validateProject(Project project) {
        if (project == null) {
            throw new IllegalArgumentException("Project cannot be null");
        }

        validateProjectName(project.getName());
        validateProjectDescription(project.getDescription());
        validateProjectDates(project.getStartDate(), project.getEndDate());
    }

    private void validateProjectName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Project name cannot be null or empty");
        }
        if (name.length() < 2) {
            throw new IllegalArgumentException("Project name must be at least 2 characters long");
        }
        if (name.length() > 50) {
            throw new IllegalArgumentException("Project name cannot exceed 50 characters");
        }
    }

    private void validateProjectDescription(String description) {
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Project description cannot be null or empty");
        }
        if (description.length() > 100) {
            throw new IllegalArgumentException("Project description cannot exceed 100 characters");
        }
    }

    private void validateProjectDates(LocalDate startDate, LocalDate endDate) {
        if (startDate == null) {
            throw new IllegalArgumentException("Project start date cannot be null");
        }

        // Allow end date to be null (ongoing projects)
        if (endDate != null && startDate.isAfter(endDate)) {
            throw new InvalidProjectException("Project start date cannot be after end date");
        }

        // Optionally, prevent creating projects with start dates in the far past
        LocalDate oneYearAgo = LocalDate.now().minusYears(1);
        if (startDate.isBefore(oneYearAgo)) {
            throw new InvalidProjectException("Project start date cannot be more than one year in the past");
        }
    }

    // Inner class for project statistics
    public static class ProjectStatistics {
        private final int projectId;
        private final String projectName;
        private final long totalTasks;
        private final long completedTasks;
        private final long pendingTasks;
        private final double completionPercentage;

        public ProjectStatistics(int projectId, String projectName, long totalTasks,
                                 long completedTasks, long pendingTasks, double completionPercentage) {
            this.projectId = projectId;
            this.projectName = projectName;
            this.totalTasks = totalTasks;
            this.completedTasks = completedTasks;
            this.pendingTasks = pendingTasks;
            this.completionPercentage = completionPercentage;
        }

        // Getters
        public int getProjectId() { return projectId; }
        public String getProjectName() { return projectName; }
        public long getTotalTasks() { return totalTasks; }
        public long getCompletedTasks() { return completedTasks; }
        public long getPendingTasks() { return pendingTasks; }
        public double getCompletionPercentage() { return completionPercentage; }

        @Override
        public String toString() {
            return String.format("Project: %s | Total: %d | Completed: %d | Pending: %d | Progress: %.1f%%",
                    projectName, totalTasks, completedTasks, pendingTasks, completionPercentage);
        }
    }
}
