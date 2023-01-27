package com.habla.exception;

public class SessionNotFoundException extends RuntimeException {
    public SessionNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
