package com.staccato.config.log;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
public class TraceAspect {
    @Before("@within(com.staccato.config.log.annotation.Trace)")
    public void doTrace(JoinPoint joinPoint) {
        Signature signature = joinPoint.getSignature();
        String className = signature.getDeclaringTypeName();
        String methodName = signature.getName();
        log.info(LogForm.TRACE_LOGGING_FORM, className, methodName);
    }
}
