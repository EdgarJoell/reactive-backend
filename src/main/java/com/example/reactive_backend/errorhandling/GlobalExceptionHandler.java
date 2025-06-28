package com.example.reactive_backend.errorhandling;

import com.example.reactive_backend.errorhandling.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
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
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Mono<ErrorAdviceDto> returnCouldNotInsertErrorAdvice(CouldNotInsertException exception, ServerWebExchange exchange) {
        ErrorAdviceDto error = ErrorAdviceDto.builder()
                .path(exchange.getRequest().getPath().toString())
                .message(exception.getMessage())
                .error(HttpStatus.INTERNAL_SERVER_ERROR)
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .build();

        return Mono.just(error);
    }

    @ExceptionHandler(CouldNotUpdateException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Mono<ErrorAdviceDto> returnCouldNotUpdateErrorAdvice(CouldNotUpdateException exception, ServerWebExchange exchange) {
        ErrorAdviceDto error = ErrorAdviceDto.builder()
                .path(exchange.getRequest().getPath().toString())
                .message(exception.getMessage())
                .error(HttpStatus.INTERNAL_SERVER_ERROR)
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .build();

        return Mono.just(error);
    }

    @ExceptionHandler(CouldNotDeleteException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Mono<ErrorAdviceDto> returnCouldNotDeleteErrorAdvice(CouldNotDeleteException exception, ServerWebExchange exchange) {
        ErrorAdviceDto error = ErrorAdviceDto.builder()
                .path(exchange.getRequest().getPath().toString())
                .message(exception.getMessage())
                .error(HttpStatus.INTERNAL_SERVER_ERROR)
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .build();

        return Mono.just(error);
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Mono<ErrorAdviceDto> returnBadRequestErrorAdvice(BadRequestException exception, ServerWebExchange exchange) {
        ErrorAdviceDto error = ErrorAdviceDto.builder()
                .path(exchange.getRequest().getPath().toString())
                .message(exception.getMessage())
                .error(HttpStatus.BAD_REQUEST)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .build();

        return Mono.just(error);
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Mono<ErrorAdviceDto> returnGlobalExceptionErrorAdvice(RuntimeException exception, ServerWebExchange exchange) {
        ErrorAdviceDto error = ErrorAdviceDto.builder()
                .path(exchange.getRequest().getPath().toString())
                .message(exception.getMessage())
                .error(HttpStatus.INTERNAL_SERVER_ERROR)
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .build();

        return Mono.just(error);
    }
}
