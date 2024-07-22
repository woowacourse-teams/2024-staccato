package com.staccato.travel.service;

import static org.junit.jupiter.api.Assertions.assertAll;

import java.time.LocalDate;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.staccato.member.domain.Member;
import com.staccato.member.repository.MemberRepository;
import com.staccato.travel.domain.Mate;
import com.staccato.travel.repository.MateRepository;
import com.staccato.travel.service.dto.request.TravelRequest;
import com.staccato.travel.service.dto.response.TravelResponse;
import com.staccato.util.DatabaseCleanerExtension;

@ExtendWith(DatabaseCleanerExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class TravelServiceTest {
    @Autowired
    private TravelService travelService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MateRepository mateRepository;

    @DisplayName("여행 상세 정보를 기반으로, 여행 상세를 생성하고 작성자를 저장한다.")
    @Test
    void createTravel() {
        // given
        TravelRequest travelRequest = new TravelRequest(
                "https://example.com/travels/geumohrm.jpg",
                "2023 여름 휴가",
                "친구들과 함께한 여름 휴가 여행",
                LocalDate.of(2023, 7, 1),
                LocalDate.of(2023, 7, 10)
        );
        Member member = memberRepository.save(Member.builder().nickname("staccato").build());

        // when
        TravelResponse travel = travelService.createTravel(travelRequest, member.getId());
        Mate mate = mateRepository.findAll().get(0);

        // then
        assertAll(
                () -> Assertions.assertThat(mate.getMember().getId()).isEqualTo(member.getId()),
                () -> Assertions.assertThat(mate.getTravel().getId()).isEqualTo(travel.travelId())
        );
    }
}
