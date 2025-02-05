package com.staccato.config.log;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.staccato.fixture.Member.MemberFixture;
import com.staccato.member.domain.Member;
import com.staccato.member.repository.MemberRepository;
import com.staccato.memory.service.MemoryService;
import com.staccato.memory.service.dto.request.MemoryReadRequest;
import io.micrometer.core.instrument.MeterRegistry;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
public class ReadAllCategoryMetricsAspectTest {

    @Autowired
    private MemoryService memoryService;
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
        MemoryReadRequest memoryReadRequest = new MemoryReadRequest("term", "oldest");

        // when
        memoryService.readAllMemories(member, memoryReadRequest);

        // then
        double filterCount = meterRegistry.counter("category_filter_count",
                "class", MemoryService.class.getName(),
                "method", "readAllMemories",
                "filter", "term").count();
        double sortCount = meterRegistry.counter("category_sort_count",
                "class", MemoryService.class.getName(),
                "method", "readAllMemories",
                "sort", "OLDEST").count();
        assertAll(
                () -> assertThat(filterCount).isEqualTo(1.0),
                () -> assertThat(sortCount).isEqualTo(1.0)
        );
    }
}

