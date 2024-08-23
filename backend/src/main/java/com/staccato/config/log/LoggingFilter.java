package com.staccato.config.log;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StopWatch;
import org.springframework.web.filter.OncePerRequestFilter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class LoggingFilter extends OncePerRequestFilter {
    private static final String IDENTIFIER = "request_id";
    private static final List<String> WHITE_LIST = List.of("/h2-console/**", "/favicon/**", "/swagger-ui/**", "/v3/api-docs/**");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        MDC.put(IDENTIFIER, UUID.randomUUID().toString());
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        try {
            filterChain.doFilter(request, response);
        } finally {
            stopWatch.stop();
            log.info(LogForm.REQUEST_LOGGING_FORM,
                    response.getStatus(),
                    request.getMethod(),
                    request.getRequestURI(),
                    tokenExists(token),
                    stopWatch.getTotalTimeMillis());
            MDC.clear();
        }
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
