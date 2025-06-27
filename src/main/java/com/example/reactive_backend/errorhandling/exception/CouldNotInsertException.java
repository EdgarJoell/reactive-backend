package com.example.reactive_backend.errorhandling.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class CouldNotInsertException extends RuntimeException {
    public CouldNotInsertException(String message) {
        super(message);
    }
}
