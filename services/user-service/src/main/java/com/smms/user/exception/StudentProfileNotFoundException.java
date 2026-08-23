package com.smms.user.exception;

public class StudentProfileNotFoundException extends RuntimeException {

    public StudentProfileNotFoundException(String message) {
        super(message);
    }
}