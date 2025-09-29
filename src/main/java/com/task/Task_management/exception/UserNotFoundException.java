package com.task.Task_management.exception;

public class UserNotFoundException extends RuntimeException{

    public UserNotFoundException(String message){
        super(message);
    }

    public UserNotFoundException(int userId){
        super("User not found with Id: " + userId);
    }

    public UserNotFoundException(String field, String value){
        super("User not find with " + field + ": " + value);
    }

}
