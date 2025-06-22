package com.example.reactive_backend.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
        HttpStatus httpStatus = HttpStatus.NOT_FOUND;
    }
}
