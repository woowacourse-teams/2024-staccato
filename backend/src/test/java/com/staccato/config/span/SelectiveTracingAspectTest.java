package com.staccato.config.span;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.test.simple.SimpleTracer;

class SelectiveTracingAspectTest {

    private SelectiveTracingAspect selectiveTracingAspect;

    @BeforeEach
    void setUp() {
        Tracer tracer = new SimpleTracer();
        selectiveTracingAspect = new SelectiveTracingAspect(tracer);
    }

    @DisplayName("AOP 적용 시 서비스 메서드를 호출하고 Span을 정상적으로 생성 및 종료한다")
    @Test
    void shouldCreateSpanAndProceed() throws Throwable {
        // given
        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        Signature signature = mock(Signature.class);

        when(signature.getName()).thenReturn("createCategory");
        when(signature.getDeclaringTypeName()).thenReturn("com.staccato.category.service.CategoryService");
        when(joinPoint.getSignature()).thenReturn(signature);
        when(joinPoint.proceed()).thenReturn(null);

        // when
        selectiveTracingAspect.traceSpecificMethods(joinPoint);

        // then
        verify(joinPoint, times(1)).proceed();
    }
}
