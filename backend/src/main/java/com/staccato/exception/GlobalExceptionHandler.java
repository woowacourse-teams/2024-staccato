package com.staccato.exception;

import java.time.format.DateTimeParseException;

import jakarta.validation.ConstraintViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity<ExceptionResponse> handleDateTimeParseException(DateTimeParseException e) {
        return ResponseEntity.badRequest()
                .body(new ExceptionResponse(HttpStatus.BAD_REQUEST.toString(), "올바르지 않은 날짜 형식입니다."));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ExceptionResponse> handleConstraintViolationException(ConstraintViolationException e) {
        return ResponseEntity.badRequest()
                .body(new ExceptionResponse(HttpStatus.BAD_REQUEST.toString(), e.getMessage()));
    }
}
