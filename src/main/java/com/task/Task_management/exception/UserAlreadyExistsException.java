package com.task.Task_management.exception;

public class UserAlreadyExistsException extends RuntimeException{

    public UserAlreadyExistsException(String message){
        super(message);
    }

    public UserAlreadyExistsException(String message, String email) {
        super(message + ": " + email);
    }
}
