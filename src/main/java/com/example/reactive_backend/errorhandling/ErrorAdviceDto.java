package com.example.reactive_backend.errorhandling;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Data
@Jacksonized
@Builder(toBuilder = true)
@RequiredArgsConstructor
@AllArgsConstructor
public class ErrorAdviceDto {
    private HttpStatus error;
    private int statusCode;
    private String path;
    private String message;
}
