package com.staccato.config.log;

import java.math.BigDecimal;
import java.time.LocalDate;
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

    static class TestCase {
        String description;
        LocalDateTime visitedAt;
        double expectedPast;
        double expectedNow;
        double expectedFuture;

        public TestCase(String description, LocalDateTime visitedAt, double expectedPast, double expectedNow, double expectedFuture) {
            this.description = description;
            this.visitedAt = visitedAt;
            this.expectedPast = expectedPast;
            this.expectedNow = expectedNow;
            this.expectedFuture = expectedFuture;
        }
    }

    @DisplayName("기록되는 시점을 매트릭을 통해 표현 할 수 있습니다.")
    @TestFactory
    Stream<DynamicTest> createMomentMetricsAspect() {
        Member member = saveMember();
        Memory memory = saveMemory(member);
        LocalDateTime now = LocalDateTime.now();

        registerMetrics(memory, member, now);

        List<TestCase> testCases = List.of(
                new TestCase("과거 기록 Counter를 매트릭에 추가합니다.", now.minusDays(5), 2.0, 1.0, 1.0),
                new TestCase("현재 기록 Counter를 매트릭에 추가합니다.", now, 2.0, 2.0, 1.0),
                new TestCase("미래 기록 Counter를 매트릭에 추가합니다.", now.plusDays(1), 2.0, 2.0, 2.0),
                new TestCase("미래 기록 Counter를 매트릭에 추가합니다.", now.plusDays(2), 2.0, 2.0, 3.0)
        );

        return testCases.stream().map(tc ->
                dynamicTest(tc.description, () -> {
                    MomentRequest momentRequest = createRequest(memory.getId(), tc.visitedAt);
                    momentService.createMoment(momentRequest, member);

                    double pastCount = meterRegistry.get("staccato_record_viewpoint")
                            .tag("viewPoint", "past")
                            .counter().count();
                    double nowCount = meterRegistry.get("staccato_record_viewpoint")
                            .tag("viewPoint", "now")
                            .counter().count();
                    double futureCount = meterRegistry.get("staccato_record_viewpoint")
                            .tag("viewPoint", "future")
                            .counter().count();

                    assertAll(
                            () -> assertThat(tc.expectedPast).isEqualTo(pastCount),
                            () -> assertThat(tc.expectedNow).isEqualTo(nowCount),
                            () -> assertThat(tc.expectedFuture).isEqualTo(futureCount)
                    );
                })
        );
    }

    private Member saveMember() {
        return memberRepository.save(MemberFixture.create());
    }

    private Memory saveMemory(Member member) {
        Memory memory = MemoryFixture.create(LocalDate.now().minusDays(50), LocalDate.now().plusDays(50));
        memory.addMemoryMember(member);
        return memoryRepository.save(memory);
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

    private void registerMetrics(Memory memory, Member member, LocalDateTime now) {
        MomentRequest pastRequest = createRequest(memory.getId(), now.minusDays(1));
        MomentRequest nowRequest = createRequest(memory.getId(), now);
        MomentRequest futureRequest = createRequest(memory.getId(), now.plusDays(1));
        momentService.createMoment(pastRequest, member);
        momentService.createMoment(nowRequest, member);
        momentService.createMoment(futureRequest, member);
    }
}
