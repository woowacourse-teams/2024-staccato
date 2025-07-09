package com.staccato.config.aspect;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Optional;
import jakarta.persistence.OptimisticLockException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Component;
import com.staccato.config.aspect.annotation.OptimisticLockMessage;
import com.staccato.exception.ConflictException;

@Aspect
@Component
public class OptimisticLockHandlerAspect {

    @Around("execution(* com.staccato..service.*.*(..)) && @annotation(org.springframework.transaction.annotation.Transactional)")
    public Object wrapOptimisticLock(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            return joinPoint.proceed();
        } catch (OptimisticLockException | OptimisticLockingFailureException ex) {
            Optional<String> message = getMessage(joinPoint);
            if (message.isEmpty()) {
                throw new ConflictException(ex);
            }
            throw new ConflictException(message.get(), ex);
        }
    }

    private static Optional<String> getMessage(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        OptimisticLockMessage annotation = method.getAnnotation(OptimisticLockMessage.class);
        if (Objects.isNull(annotation)) {
            return Optional.empty();
        }
        return Optional.of(annotation.value());
    }
}
