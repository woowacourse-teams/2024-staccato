package com.staccato.config.log;

import com.staccato.memory.service.dto.request.CategoryReadRequest;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import com.staccato.member.domain.Member;
import com.staccato.memory.service.CategoryFilter;
import com.staccato.memory.service.CategoryService;
import com.staccato.memory.service.CategorySort;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;

@Aspect
@Component
@RequiredArgsConstructor
public class ReadAllCategoryMetricsAspect {

    private final MeterRegistry meterRegistry;

    @Pointcut("execution(public * com.staccato.memory.service.CategoryService.readAllCategories(..)) && args(member, categoryReadRequest)")
    public void createCategoryPointcut(Member member, CategoryReadRequest categoryReadRequest) {
    }

    @AfterReturning(pointcut = "createCategoryPointcut(member, categoryReadRequest)", returning = "result", argNames = "member,categoryReadRequest,result")
    public void afterSuccessfulReadCategory(Member member, CategoryReadRequest categoryReadRequest, Object result) {
        for (CategoryFilter filter : categoryReadRequest.getFilters()) {
            countFilter(filter);
        }
        countSort(categoryReadRequest.getSort());
    }

    private void countFilter(CategoryFilter filter) {
        Counter.builder("category_filter_count")
                .tag("class", CategoryService.class.getName())
                .tag("method", "readAllMemories")
                .tag("filter", filter.getName())
                .description("counts for filter usage of categories")
                .register(meterRegistry).increment();
    }

    private void countSort(CategorySort sort) {
        Counter.builder("category_sort_count")
                .tag("class", CategoryService.class.getName())
                .tag("method", "readAllMemories")
                .tag("sort", sort.getName())
                .description("counts for sort usage of categories")
                .register(meterRegistry).increment();
    }
}
