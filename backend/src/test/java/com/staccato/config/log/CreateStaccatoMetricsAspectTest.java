package com.staccato.config.log;

import com.staccato.staccato.service.dto.request.StaccatoRequest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.staccato.ServiceSliceTest;
import com.staccato.fixture.Member.MemberFixture;
import com.staccato.fixture.category.CategoryFixture;
import com.staccato.member.domain.Member;
import com.staccato.member.repository.MemberRepository;
import com.staccato.category.domain.Category;
import com.staccato.category.repository.CategoryRepository;
import com.staccato.staccato.service.StaccatoService;
import io.micrometer.core.instrument.MeterRegistry;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

class CreateStaccatoMetricsAspectTest extends ServiceSliceTest {

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private StaccatoService staccatoService;
    @Autowired
    private MeterRegistry meterRegistry;

    @DisplayName("기록 상의 날짜를 현재를 기준으로 과거 혹은 미래 인지 매트릭을 통해 표현 할 수 있습니다.")
    @TestFactory
    List<DynamicTest> createStaccatoMetricsAspect() {
        Member member = saveMember();
        Category category = saveCategory(member);
        LocalDateTime now = LocalDateTime.now();

        return List.of(
                dynamicTest("기록 상의 날짜가 과거인 기록과 미래인 기록을 매트릭에 등록합니다.", () -> {
                    // given
                    StaccatoRequest pastRequest = createRequest(category.getId(), now.minusDays(2));
                    StaccatoRequest futureRequest = createRequest(category.getId(), now.plusDays(2));

                    //when
                    staccatoService.createStaccato(pastRequest, member);
                    staccatoService.createStaccato(futureRequest, member);

                    //then
                    assertAll(
                            () -> assertThat(getPastCount()).isEqualTo(1.0),
                            () -> assertThat(getFutureCount()).isEqualTo(1.0)
                    );
                }),
                dynamicTest("기록 상의 날짜가 과거인 기록 작성 요청 → 누적: past:2.0, future:1.0", () -> {
                    // given
                    StaccatoRequest staccatoRequest = createRequest(category.getId(), now.minusDays(3));

                    // when
                    staccatoService.createStaccato(staccatoRequest, member);

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

    private Category saveCategory(Member member) {
        Category category = CategoryFixture.create();
        category.addCategoryMember(member);
        return categoryRepository.save(category);
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

    private StaccatoRequest createRequest(Long memoryId, LocalDateTime localDateTime) {
        return new StaccatoRequest(
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
