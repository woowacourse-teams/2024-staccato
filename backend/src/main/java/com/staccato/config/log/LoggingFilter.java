package com.staccato.config.log;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class LoggingFilter extends OncePerRequestFilter {
    private static final List<String> WHITE_LIST = List.of("/h2-console/**", "/favicon/**", "/swagger-ui/**", "/v3/api-docs/**");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        long startTime = System.currentTimeMillis();
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        filterChain.doFilter(request, response);
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        log.info(LogForm.REQUEST_LOGGING_FORM,
                request.getMethod(),
                request.getRequestURI(),
                tokenExists(token),
                response.getStatus(),
                duration);
    }

    private boolean tokenExists(String token) {
        return !(Objects.isNull(token) || token.isBlank());
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String requestURI = request.getRequestURI();
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        return WHITE_LIST.stream().anyMatch(path -> antPathMatcher.match(path, requestURI));
    }
}
