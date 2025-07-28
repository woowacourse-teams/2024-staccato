package com.staccato.config.aspect;

import java.lang.reflect.Method;
import java.util.Optional;
import jakarta.persistence.OptimisticLockException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Component;
import com.staccato.config.aspect.annotation.OptimisticLockHandler;
import com.staccato.exception.ConflictException;

@Aspect
@Component
public class OptimisticLockAspect {

    @Around("@annotation(org.springframework.transaction.annotation.Transactional)")
    public Object wrapOptimisticLock(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            return joinPoint.proceed();
        } catch (OptimisticLockException | OptimisticLockingFailureException ex) {
            throw getMessage(joinPoint)
                    .map(message -> new ConflictException(message, ex))
                    .orElseThrow(() -> new ConflictException(ex));
        }
    }

    private Optional<String> getMessage(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        OptimisticLockHandler annotation = method.getAnnotation(OptimisticLockHandler.class);

        return Optional.ofNullable(annotation)
                .map(OptimisticLockHandler::value);
    }
}
