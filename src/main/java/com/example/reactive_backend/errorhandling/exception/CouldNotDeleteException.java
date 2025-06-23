package com.example.reactive_backend.errorhandling.exception;

public class CouldNotDeleteException extends RuntimeException {

    public CouldNotDeleteException(String message) {
        super(message);
    }
}
