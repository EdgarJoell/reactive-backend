package com.example.reactive_backend.exception;

import org.springframework.http.HttpStatus;

public class CouldNotInsertException extends RuntimeException {
    public CouldNotInsertException(String message, HttpStatus httpStatus) {
        super(message);
    }
}
