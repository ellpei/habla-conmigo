package com.habla.exception;

public class InvalidGameStateException extends RuntimeException {
    public InvalidGameStateException(String errorMessage) {
        super(errorMessage);
    }

}
