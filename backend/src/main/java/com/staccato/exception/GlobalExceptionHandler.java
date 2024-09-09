package com.staccato.exception;

import java.util.Optional;

import jakarta.validation.ConstraintViolationException;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.staccato.config.log.LogForm;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final String INTERNAL_SERVER_ERROR_MESSAGE = "예기치 못한 서버 오류입니다. 다시 시도해주세요.";

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ApiResponse(responseCode = "400")
    public ResponseEntity<ExceptionResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        String exceptionMessage = "올바르지 않은 쿼리 스트링 형식입니다.";
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST.toString(), exceptionMessage);
        log.warn(LogForm.EXCEPTION_LOGGING_FORM, exceptionResponse, e.getMessage());
        return ResponseEntity.badRequest().body(exceptionResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ApiResponse(responseCode = "400")
    public ResponseEntity<ExceptionResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String exceptionMessage = Optional.ofNullable(e.getBindingResult().getFieldError())
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .orElse("요청 형식이 잘못되었습니다.");
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST.toString(), exceptionMessage);
        log.warn(LogForm.EXCEPTION_LOGGING_FORM, exceptionResponse, e.getMessage());
        return ResponseEntity.badRequest().body(exceptionResponse);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ApiResponse(responseCode = "400")
    public ResponseEntity<ExceptionResponse> handleConstraintViolationException(ConstraintViolationException e) {
        String exceptionMessage = e.getConstraintViolations()
                .iterator()
                .next()
                .getMessage();
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST.toString(), exceptionMessage);
        log.warn(LogForm.EXCEPTION_LOGGING_FORM, exceptionResponse, e.getMessage());
        return ResponseEntity.badRequest().body(exceptionResponse);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ApiResponse(responseCode = "400")
    public ResponseEntity<ExceptionResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        String exceptionMessage = "요청 본문을 읽을 수 없습니다. 올바른 형식으로 데이터를 제공해주세요.";
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST.toString(), exceptionMessage);
        log.warn(LogForm.EXCEPTION_LOGGING_FORM, exceptionResponse, e.getMessage());
        return ResponseEntity.badRequest().body(exceptionResponse);
    }

    @ExceptionHandler(S3Exception.class)
    @ApiResponse(responseCode = "400")
    public ResponseEntity<ExceptionResponse> handleS3Exception(S3Exception e) {
        String exceptionMessage = "이미지 처리에 실패했습니다.";
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST.toString(), exceptionMessage);
        log.warn(LogForm.EXCEPTION_LOGGING_FORM, exceptionResponse, e.getMessage());
        return ResponseEntity.badRequest().body(exceptionResponse);
    }

    @ExceptionHandler(StaccatoException.class)
    @ApiResponse(responseCode = "400")
    public ResponseEntity<ExceptionResponse> handleStaccatoException(StaccatoException e) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST.toString(), e.getMessage());
        log.info(LogForm.CUSTOM_EXCEPTION_LOGGING_FORM, exceptionResponse);
        return ResponseEntity.badRequest().body(exceptionResponse);
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ApiResponse(description = "사용자 인증 실패", responseCode = "401")
    public ResponseEntity<ExceptionResponse> handleUnauthorizedException(UnauthorizedException e) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.UNAUTHORIZED.toString(), e.getMessage());
        log.warn(LogForm.CUSTOM_EXCEPTION_LOGGING_FORM, exceptionResponse);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(exceptionResponse);
    }

    @ExceptionHandler(ForbiddenException.class)
    @ApiResponse(description = "사용자가 권한을 가지고 있지 않은 작업을 시도 시 발생", responseCode = "403")
    public ResponseEntity<ExceptionResponse> handleForbiddenException(ForbiddenException e) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.FORBIDDEN.toString(), e.getMessage());
        log.warn(LogForm.CUSTOM_EXCEPTION_LOGGING_FORM, exceptionResponse);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(exceptionResponse);
    }

    @ExceptionHandler(RuntimeException.class)
    @ApiResponse(responseCode = "500")
    public ResponseEntity<ExceptionResponse> handleInternalServerErrorException(RuntimeException e) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR.toString(), INTERNAL_SERVER_ERROR_MESSAGE);
        log.error(LogForm.ERROR_LOGGING_FORM, exceptionResponse, e.getMessage());
        return ResponseEntity.internalServerError().body(exceptionResponse);
    }

    @ExceptionHandler(CannotCreateTransactionException.class)
    @ApiResponse(responseCode = "500")
    public ResponseEntity<ExceptionResponse> handleCannotCreateTransactionException(CannotCreateTransactionException e) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR.toString(), INTERNAL_SERVER_ERROR_MESSAGE);
        log.error(LogForm.ERROR_LOGGING_FORM, exceptionResponse, e.getMessage(), e.getStackTrace());
        return ResponseEntity.internalServerError().body(exceptionResponse);
    }

    @ExceptionHandler(DataAccessException.class)
    @ApiResponse(responseCode = "500")
    public ResponseEntity<ExceptionResponse> handleDataAccessException(DataAccessException e) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR.toString(), INTERNAL_SERVER_ERROR_MESSAGE);
        log.error(LogForm.ERROR_LOGGING_FORM, exceptionResponse, e.getMessage(), e.getStackTrace());
        return ResponseEntity.internalServerError().body(exceptionResponse);
    }

    @ExceptionHandler(TransactionSystemException.class)
    @ApiResponse(responseCode = "500")
    public ResponseEntity<ExceptionResponse> handleTransactionSystemException(TransactionSystemException e) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR.toString(), INTERNAL_SERVER_ERROR_MESSAGE);
        log.error(LogForm.ERROR_LOGGING_FORM, exceptionResponse, e.getMessage(), e.getStackTrace());
        return ResponseEntity.internalServerError().body(exceptionResponse);
    }
}
