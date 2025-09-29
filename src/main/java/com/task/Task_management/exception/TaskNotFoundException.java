package com.task.Task_management.exception;

public class TaskNotFoundException extends RuntimeException {
    public TaskNotFoundException(String message) {
        super(message);
    }

    public TaskNotFoundException(int taskId) {
        super("Task not found with ID: " + taskId);
    }
}
