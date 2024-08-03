package com.staccato.exception;

import java.time.format.DateTimeParseException;
import java.util.Optional;

import jakarta.validation.ConstraintViolationException;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity<ExceptionResponse> handleDateTimeParseException(DateTimeParseException e) {
        String errorMessage = "올바르지 않은 날짜 형식입니다.";
        log.warn("ExceptionType : {}, ExceptionMessage : {}", e, errorMessage);
        return ResponseEntity.badRequest()
                .body(new ExceptionResponse(HttpStatus.BAD_REQUEST.toString(), errorMessage));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleValidationException(MethodArgumentNotValidException e) {
        String message = Optional.ofNullable(e.getBindingResult().getFieldError())
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .orElse("요청 형식이 잘못되었습니다.");
        log.warn("ExceptionType : {}, ExceptionMessage : {}", e, message);
        return ResponseEntity.badRequest()
                .body(new ExceptionResponse(HttpStatus.BAD_REQUEST.toString(), message));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ExceptionResponse> handleConstraintViolationException(ConstraintViolationException e) {
        String errorMessage = e.getConstraintViolations()
                .iterator()
                .next()
                .getMessage();
        log.warn("ExceptionType : {}, ExceptionMessage : {}", e, errorMessage);
        return ResponseEntity.badRequest()
                .body(new ExceptionResponse(HttpStatus.BAD_REQUEST.toString(), errorMessage));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ExceptionResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        String errorMessage = "요청 본문을 읽을 수 없습니다. 올바른 형식으로 데이터를 제공해주세요.";
        log.warn("ExceptionType : {}, ExceptionMessage : {}", e, errorMessage);
        return ResponseEntity.badRequest()
                .body(new ExceptionResponse(HttpStatus.BAD_REQUEST.toString(), errorMessage));
    }

    @ExceptionHandler(StaccatoException.class)
    @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
    public ResponseEntity<ExceptionResponse> handleStaccatoException(StaccatoException e) {
        log.warn("ExceptionType : {}, ExceptionMessage : {}", e, e.getMessage());
        return ResponseEntity.badRequest()
                .body(new ExceptionResponse(HttpStatus.BAD_REQUEST.toString(), e.getMessage()));
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ApiResponse(responseCode = "401", content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
    public ResponseEntity<ExceptionResponse> handleUnauthorizedException(UnauthorizedException e) {
        log.warn("ExceptionType : {}, ExceptionMessage : {}", e, e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ExceptionResponse(HttpStatus.UNAUTHORIZED.toString(), e.getMessage()));
    }


    @ExceptionHandler(ForbiddenException.class)
    @ApiResponse(responseCode = "403", content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
    public ResponseEntity<ExceptionResponse> handleForbiddenException(ForbiddenException e) {
        log.warn("ExceptionType : {}, ExceptionMessage : {}", e, e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ExceptionResponse(HttpStatus.UNAUTHORIZED.toString(), e.getMessage()));
    }

    @ExceptionHandler(RuntimeException.class)
    @ApiResponse(responseCode = "500", content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
    public ResponseEntity<ExceptionResponse> handleInternalServerErrorException(RuntimeException e) {
        log.warn("ExceptionType : {}, ExceptionMessage : {}", e, e.getMessage());
        return ResponseEntity.internalServerError()
                .body(new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "예기치 못한 서버 오류입니다. 다시 시도해주세요."));
    }
}
