package com.staccato.config.log;


import java.time.LocalDateTime;
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
    private static final long TIME_THRESHOLD_MINUTES = 30;

    private final MeterRegistry meterRegistry;

    @Pointcut("execution(public * com.staccato.moment.service.MomentService.createMoment(..)) && args(momentRequest, member)")
    public void createMomentPointcut(MomentRequest momentRequest, Member member) {
    }

    @AfterReturning(pointcut = "createMomentPointcut(momentRequest, member)", returning = "result")
    public void afterSuccessfulCreateMoment(MomentRequest momentRequest, Member member, Object result) {
        LocalDateTime visitedAt = momentRequest.visitedAt();
        LocalDateTime now = LocalDateTime.now();
        if (!isPastDateTime(visitedAt, now) && !isFutureDateTime(visitedAt, now)) {
            recordCounter("now");
            return;
        }
        checkViewPoint(visitedAt, now);
    }

    private void checkViewPoint(LocalDateTime visitedAt, LocalDateTime now) {
        if (isPastDateTime(visitedAt, now)) {
            recordCounter("past");
            return;
        }
        recordCounter("future");
    }

    private boolean isFutureDateTime(LocalDateTime visitedAt, LocalDateTime now) {
        return visitedAt.isAfter(now.plusMinutes(TIME_THRESHOLD_MINUTES));
    }

    private boolean isPastDateTime(LocalDateTime visitedAt, LocalDateTime now) {
        return visitedAt.isBefore(now.minusMinutes(TIME_THRESHOLD_MINUTES));
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
