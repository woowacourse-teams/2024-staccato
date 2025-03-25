package com.staccato.config.log;

import com.staccato.category.service.dto.request.CategoryReadRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.staccato.ServiceSliceTest;
import com.staccato.fixture.member.MemberFixture;
import com.staccato.member.domain.Member;
import com.staccato.member.repository.MemberRepository;
import com.staccato.category.service.CategoryService;
import io.micrometer.core.instrument.MeterRegistry;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class ReadAllCategoryMetricsAspectTest extends ServiceSliceTest {

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MeterRegistry meterRegistry;

    @DisplayName("필터와 정렬 조건마다 호출 횟수를 메트릭으로 남긴다.")
    @Test
    void testMetricsAfterServiceExecution() {
        // given
        Member member = MemberFixture.create();
        memberRepository.save(member);
        CategoryReadRequest categoryReadRequest = new CategoryReadRequest("term", "oldest");

        // when
        categoryService.readAllCategories(member, categoryReadRequest);

        // then
        double filterCount = meterRegistry.counter("category_filter_count",
                "class", CategoryService.class.getName(),
                "method", "readAllCategories",
                "filter", "term").count();
        double sortCount = meterRegistry.counter("category_sort_count",
                "class", CategoryService.class.getName(),
                "method", "readAllCategories",
                "sort", "OLDEST").count();
        assertAll(
                () -> assertThat(filterCount).isEqualTo(1.0),
                () -> assertThat(sortCount).isEqualTo(1.0)
        );
    }
}

