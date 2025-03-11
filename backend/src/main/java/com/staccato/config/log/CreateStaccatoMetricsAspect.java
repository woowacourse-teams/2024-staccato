package com.staccato.config.log;


import com.staccato.staccato.service.dto.request.StaccatoRequest;
import java.time.LocalDate;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import com.staccato.member.domain.Member;
import com.staccato.staccato.service.StaccatoService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;

@Aspect
@Component
@RequiredArgsConstructor
public class CreateStaccatoMetricsAspect {

    private final MeterRegistry meterRegistry;

    @Pointcut("execution(public * com.staccato.staccato.service.StaccatoService.createStaccato(..)) && args(staccatoRequest, member)")
    public void createStaccatoPointcut(StaccatoRequest staccatoRequest, Member member) {
    }

    @AfterReturning(pointcut = "createStaccatoPointcut(staccatoRequest, member)", returning = "result")
    public void afterSuccessfulCreateStaccato(StaccatoRequest staccatoRequest, Member member, Object result) {
        LocalDate visitedAt = staccatoRequest.visitedAt().toLocalDate();
        LocalDate now = LocalDate.now();
        if (isPastDate(visitedAt, now)) {
            recordCounter("past");
            return;
        }
        if (isFutureDate(visitedAt, now)) {
            recordCounter("future");
            return;
        }
        recordCounter("now");
    }

    private boolean isPastDate(LocalDate visitedAt, LocalDate now) {
        return visitedAt.isBefore(now);
    }

    private boolean isFutureDate(LocalDate visitedAt, LocalDate now) {
        return visitedAt.isAfter(now);
    }

    private void recordCounter(String viewPoint) {
        Counter.builder("staccato_record_viewpoint")
                .tag("class", StaccatoService.class.getName())
                .tag("method", "createStaccato")
                .tag("viewPoint", viewPoint)
                .description("counts different view points for Staccato Record")
                .register(meterRegistry).increment();
    }
}
