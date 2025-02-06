package com.staccato.config.log;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import com.staccato.member.domain.Member;
import com.staccato.memory.service.MemoryFilter;
import com.staccato.memory.service.MemoryService;
import com.staccato.memory.service.MemorySort;
import com.staccato.memory.service.dto.request.MemoryReadRequest;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;

@Aspect
@Component
@RequiredArgsConstructor
public class ReadAllCategoryMetricsAspect {

    private final MeterRegistry meterRegistry;

    @Pointcut("execution(public * com.staccato.memory.service.MemoryService.readAllMemories(..)) && args(member, memoryReadRequest)")
    public void createMemoryPointcut(Member member, MemoryReadRequest memoryReadRequest) {
    }

    @AfterReturning(pointcut = "createMemoryPointcut(member, memoryReadRequest)", returning = "result", argNames = "member,memoryReadRequest,result")
    public void afterSuccessfulReadMemory(Member member, MemoryReadRequest memoryReadRequest, Object result) {
        for (MemoryFilter filter : memoryReadRequest.getFilters()) {
            countFilter(filter);
        }
        countSort(memoryReadRequest.getSort());
    }

    private void countFilter(MemoryFilter filter) {
        Counter.builder("category_filter_count")
                .tag("class", MemoryService.class.getName())
                .tag("method", "readAllMemories")
                .tag("filter", filter.getName())
                .description("counts for filter usage of categories")
                .register(meterRegistry).increment();
    }

    private void countSort(MemorySort sort) {
        Counter.builder("category_sort_count")
                .tag("class", MemoryService.class.getName())
                .tag("method", "readAllMemories")
                .tag("sort", sort.getName())
                .description("counts for sort usage of categories")
                .register(meterRegistry).increment();
    }
}
