package com.springproject.dhVinh.SpringBootProject.exception;


public class ServicePackAlreadyExistsException extends RuntimeException {
    public ServicePackAlreadyExistsException(String message) {
        super(message);
    }
}
