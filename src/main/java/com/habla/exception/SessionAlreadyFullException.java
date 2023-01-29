package com.habla.exception;

public class SessionAlreadyFullException extends RuntimeException {
    public SessionAlreadyFullException(String errorMessage) {
        super(errorMessage);
    }

}
