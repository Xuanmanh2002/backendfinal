package com.springproject.dhVinh.SpringBootProject.exception;

public class JobAlreadyExistException extends RuntimeException{
    public JobAlreadyExistException(String message) {
        super(message);
    }
}
