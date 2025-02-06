package com.staccato.config.log;


import java.time.LocalDate;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import com.staccato.member.domain.Member;
import com.staccato.moment.service.MomentService;
import com.staccato.moment.service.dto.request.MomentRequest;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;

@Aspect
@Component
@RequiredArgsConstructor
public class CreateMomentMetricsAspect {

    private final MeterRegistry meterRegistry;

    @Pointcut("execution(public * com.staccato.moment.service.MomentService.createMoment(..)) && args(momentRequest, member)")
    public void createMomentPointcut(MomentRequest momentRequest, Member member) {
    }

    @AfterReturning(pointcut = "createMomentPointcut(momentRequest, member)", returning = "result")
    public void afterSuccessfulCreateMoment(MomentRequest momentRequest, Member member, Object result) {
        LocalDate visitedAt = momentRequest.visitedAt().toLocalDate();
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
                .tag("class", MomentService.class.getName())
                .tag("method", "createMoment")
                .tag("viewPoint", viewPoint)
                .description("counts different view points for Staccato Record")
                .register(meterRegistry).increment();
    }
}
