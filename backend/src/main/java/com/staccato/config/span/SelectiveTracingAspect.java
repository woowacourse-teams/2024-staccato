package com.staccato.config.span;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class SelectiveTracingAspect {

    private final Tracer tracer;

    @Around("execution(* com.staccato..createCategory(..)) || " +
            "execution(* com.staccato..createStaccato(..)) || " +
            "execution(* com.staccato..readAllCategories(..)) || " +
            "execution(* com.staccato..readAllStaccato(..))")
    public Object traceSpecificMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        RequestData requestData = extractRequestData();
        String spanName = createSpanName(methodName, requestData.requestUri());

        Span span = tracer.spanBuilder()
                .name(spanName)
                .start();

        try (Tracer.SpanInScope scope = tracer.withSpan(span)) {
            tagSpan(span, joinPoint, methodName, requestData);
            return joinPoint.proceed();
        } catch (Exception ex) {
            span.tag("error.type", ex.getClass().getSimpleName());
            span.error(ex);
            throw ex;
        } finally {
            span.end();
        }
    }

    private RequestData extractRequestData() {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                    .getRequest();
            return new RequestData(request.getRequestURI(), request.getQueryString());
        } catch (Exception ignored) {
            return new RequestData(null, null);
        }
    }

    private String createSpanName(String methodName, String requestUri) {
        String spanBaseName = switch (methodName) {
            case "createCategory" -> "카테고리 생성 요청";
            case "createStaccato" -> "스타카토 생성 요청";
            case "readAllCategories" -> "모든 카테고리 조회 요청";
            case "readAllStaccato" -> "모든 스타카토 조회 요청";
            default -> methodName;
        };
        if (requestUri != null) {
            return spanBaseName + " [" + requestUri + "]";
        }
        return spanBaseName;
    }

    private void tagSpan(Span span, ProceedingJoinPoint joinPoint, String methodName, RequestData requestData) {
        span.tag("operation", methodName);
        span.tag("class", joinPoint.getSignature().getDeclaringTypeName());
        span.tag("method", methodName);
        span.tag("layer", "service");
        if (requestData.requestUri() != null) {
            span.tag("http.uri", requestData.requestUri());
        }
        if (requestData.queryString() != null) {
            span.tag("query", requestData.queryString());
        }
    }

    private record RequestData(String requestUri, String queryString) {
    }
}