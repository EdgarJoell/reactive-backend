package com.example.reactive_backend.errorhandling;

import com.example.reactive_backend.errorhandling.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NotFoundException.class)
    public Mono<ErrorAdviceDto> returnNotFoundErrorAdvice(NotFoundException exception, ServerWebExchange exchange) {
        ErrorAdviceDto error = ErrorAdviceDto.builder()
                .path(exchange.getRequest().getPath().toString())
                .message(exception.getMessage())
                .error(HttpStatus.NOT_FOUND)
                .statusCode(HttpStatus.NOT_FOUND.value())
                .build();

        return Mono.just(error);
    }

    @ExceptionHandler(CouldNotInsertException.class)
    public Mono<ErrorAdviceDto> returnCouldNotInsertErrorAdvice(NotFoundException exception, ServerWebExchange exchange) {
        ErrorAdviceDto error = ErrorAdviceDto.builder()
                .path(exchange.getRequest().getPath().toString())
                .message(exception.getMessage())
                .error(HttpStatus.INTERNAL_SERVER_ERROR)
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .build();

        return Mono.just(error);
    }

    @ExceptionHandler(CouldNotUpdateException.class)
    public Mono<ErrorAdviceDto> returnCouldNotUpdateErrorAdvice(NotFoundException exception, ServerWebExchange exchange) {
        ErrorAdviceDto error = ErrorAdviceDto.builder()
                .path(exchange.getRequest().getPath().toString())
                .message(exception.getMessage())
                .error(HttpStatus.INTERNAL_SERVER_ERROR)
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .build();

        return Mono.just(error);
    }

    @ExceptionHandler(CouldNotDeleteException.class)
    public Mono<ErrorAdviceDto> returnCouldNotDeleteErrorAdvice(NotFoundException exception, ServerWebExchange exchange) {
        ErrorAdviceDto error = ErrorAdviceDto.builder()
                .path(exchange.getRequest().getPath().toString())
                .message(exception.getMessage())
                .error(HttpStatus.INTERNAL_SERVER_ERROR)
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .build();

        return Mono.just(error);
    }

    @ExceptionHandler(BadRequestException.class)
    public Mono<ErrorAdviceDto> returnBadRequestErrorAdvice(NotFoundException exception, ServerWebExchange exchange) {
        ErrorAdviceDto error = ErrorAdviceDto.builder()
                .path(exchange.getRequest().getPath().toString())
                .message(exception.getMessage())
                .error(HttpStatus.BAD_REQUEST)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .build();

        return Mono.just(error);
    }

    @ExceptionHandler(RuntimeException.class)
    public Mono<ErrorAdviceDto> returnGlobalExceptionErrorAdvice(NotFoundException exception, ServerWebExchange exchange) {
        ErrorAdviceDto error = ErrorAdviceDto.builder()
                .path(exchange.getRequest().getPath().toString())
                .message(exception.getMessage())
                .error(HttpStatus.NOT_FOUND)
                .statusCode(HttpStatus.NOT_FOUND.value())
                .build();

        return Mono.just(error);
    }
}
