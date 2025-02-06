package com.staccato.config.log;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.staccato.ServiceSliceTest;
import com.staccato.fixture.Member.MemberFixture;
import com.staccato.fixture.memory.MemoryFixture;
import com.staccato.member.domain.Member;
import com.staccato.member.repository.MemberRepository;
import com.staccato.memory.domain.Memory;
import com.staccato.memory.repository.MemoryRepository;
import com.staccato.moment.service.MomentService;
import com.staccato.moment.service.dto.request.MomentRequest;
import io.micrometer.core.instrument.MeterRegistry;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

class CreateMomentMetricsAspectTest extends ServiceSliceTest {

    @Autowired
    private MemoryRepository memoryRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MomentService momentService;
    @Autowired
    private MeterRegistry meterRegistry;

    @DisplayName("기록되는 시점을 매트릭을 통해 표현 할 수 있습니다.")
    @TestFactory
    Stream<DynamicTest> createMomentMetricsAspect() {
        Member member = saveMember();
        Memory memory = saveMemory(member);
        LocalDateTime now = LocalDateTime.now();

        return Stream.of(
                dynamicTest("과거, 미래 기록 매트릭을 등록합니다.", () -> {
                    // given
                    MomentRequest pastRequest = createRequest(memory.getId(), now.minusDays(1));
                    MomentRequest futureRequest = createRequest(memory.getId(), now.plusDays(1));

                    //when
                    momentService.createMoment(pastRequest, member);
                    momentService.createMoment(futureRequest, member);

                    //then
                    assertAll(
                            () -> assertThat(getPastCount()).isEqualTo(1.0),
                            () -> assertThat(getFutureCount()).isEqualTo(1.0)
                    );
                }),
                dynamicTest("과거 요청 → 누적: past:2, future:1", () -> {
                    // given
                    MomentRequest momentRequest = createRequest(memory.getId(), now.minusDays(1));

                    // when
                    momentService.createMoment(momentRequest, member);

                    // then
                    assertAll(
                            () -> assertThat(getPastCount()).isEqualTo(2.0),
                            () -> assertThat(getFutureCount()).isEqualTo(1.0)
                    );
                })
        );
    }

    private Member saveMember() {
        return memberRepository.save(MemberFixture.create());
    }

    private Memory saveMemory(Member member) {
        Memory memory = MemoryFixture.create();
        memory.addMemoryMember(member);
        return memoryRepository.save(memory);
    }

    private double getFutureCount() {
        return meterRegistry.get("staccato_record_viewpoint")
                .tag("viewPoint", "future")
                .counter().count();
    }

    private double getPastCount() {
        return meterRegistry.get("staccato_record_viewpoint")
                .tag("viewPoint", "past")
                .counter().count();
    }

    private MomentRequest createRequest(Long memoryId, LocalDateTime localDateTime) {
        return new MomentRequest(
                "staccatoTitle",
                "placeName",
                "address",
                BigDecimal.ONE,
                BigDecimal.ONE,
                localDateTime,
                memoryId,
                List.of());
    }
}
