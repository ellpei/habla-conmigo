package com.habla.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GameControllerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(SessionNotFoundException.class)
    public ResponseEntity<Object> sessionNotFoundException(SessionNotFoundException ex, WebRequest request) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(SessionAlreadyFullException.class)
    public ResponseEntity<Object> sessionAlreadyFullException(SessionAlreadyFullException ex, WebRequest request) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(InvalidGameStateException.class)
    public ResponseEntity<Object> invalidGameStateException(InvalidGameStateException ex, WebRequest request) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
