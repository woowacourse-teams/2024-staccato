package com.staccato.config.log;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class LoggingFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        long startTime = System.currentTimeMillis();
        filterChain.doFilter(request, response);
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        log.info(LogForm.REQUEST_LOGGING_FORM,
                request.getMethod(),
                request.getRequestURI(),
                response.getStatus(),
                duration);
    }
}
