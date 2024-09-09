package com.staccato.config.log;

import java.util.Arrays;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import com.staccato.member.domain.Member;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
public class TraceAspect {
    @Before("@annotation(com.staccato.config.log.annotation.Trace)")
    public void doTrace(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        Signature signature = joinPoint.getSignature();
        log.info(LogForm.TRACE_LOGGING_FORM, signature.getName(), excludeMemberArgs(args));
    }

    private static Object[] excludeMemberArgs(Object[] args) {
        return Arrays.stream(args)
                .filter(arg -> !(arg instanceof Member))
                .toArray();
    }
}

// 1. toString()을 활용해 멤버의 정보를 그대로 출력
// 2. 위와 같은 로직을 통해 민감한 정보들은 제외하고 출력
// 사용자에 대한 로깅은 extractFromToken 부분에서 처리하므로 사용자 추적 가능.
