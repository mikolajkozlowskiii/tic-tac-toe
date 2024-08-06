package com.javamaster.exception;

public class InvalidParamException extends Exception {

    private final String message;

    public InvalidParamException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
