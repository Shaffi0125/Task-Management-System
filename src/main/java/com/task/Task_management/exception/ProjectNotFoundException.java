package com.task.Task_management.exception;

public class ProjectNotFoundException extends RuntimeException {

    public ProjectNotFoundException(String message) {
        super(message);
    }

    public ProjectNotFoundException(int projectId) {
        super("Project not found with ID: " + projectId);
    }

}
