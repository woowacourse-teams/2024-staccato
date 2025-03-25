package com.staccato.exception;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import com.staccato.config.log.LogForm;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice(basePackages = "com.staccato.web")
public class WebExceptionHandler {

    private static final String DEFAULT_ERROR_VIEW_PATH = "error/";

    @ExceptionHandler(StaccatoException.class)
    public ModelAndView handleBadRequestException(StaccatoException e) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST.toString(), e.getMessage());
        log.info(LogForm.CUSTOM_EXCEPTION_LOGGING_FORM, exceptionResponse);
        return buildErrorView("400", "잘못된 요청입니다.");
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ModelAndView handleUnauthorizedException(UnauthorizedException e) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.UNAUTHORIZED.toString(), e.getMessage());
        log.warn(LogForm.CUSTOM_EXCEPTION_LOGGING_FORM, exceptionResponse);
        return buildErrorView("401", "유효하지 않은 링크입니다.");
    }

    @ExceptionHandler(ForbiddenException.class)
    public ModelAndView handleForbiddenException(ForbiddenException e) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.FORBIDDEN.toString(), e.getMessage());
        log.warn(LogForm.CUSTOM_EXCEPTION_LOGGING_FORM, exceptionResponse);
        return buildErrorView("403", "접근 권한이 없습니다.");
    }

    @ExceptionHandler(NotFoundException.class)
    public ModelAndView handleNotFoundException(NotFoundException e) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.NOT_FOUND.toString(), e.getMessage());
        log.warn(LogForm.CUSTOM_EXCEPTION_LOGGING_FORM, exceptionResponse);
        return buildErrorView("404", "요청하신 페이지를 찾을 수 없습니다.");
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleInternalServerError(Exception e) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                "예기치 못한 서버 오류입니다. 다시 시도해주세요.");
        log.warn(LogForm.ERROR_LOGGING_FORM, exceptionResponse, e.getMessage(), e.getStackTrace());
        return buildErrorView("500", "서버 오류가 발생했습니다.");
    }

    private ModelAndView buildErrorView(String statusCode, String message) {
        ModelAndView modelAndView = new ModelAndView(DEFAULT_ERROR_VIEW_PATH + statusCode);
        modelAndView.addObject("message", message);
        return modelAndView;
    }
}
