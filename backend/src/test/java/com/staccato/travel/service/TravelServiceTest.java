package com.staccato.travel.service;

import static org.junit.jupiter.api.Assertions.assertAll;

import java.time.LocalDate;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.staccato.ServiceSliceTest;
import com.staccato.member.domain.Member;
import com.staccato.member.repository.MemberRepository;
import com.staccato.travel.domain.TravelMember;
import com.staccato.travel.repository.TravelMemberRepostiory;
import com.staccato.travel.service.dto.request.TravelRequest;

class TravelServiceTest extends ServiceSliceTest {
    @Autowired
    private TravelService travelService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private TravelMemberRepostiory travelMemberRepostiory;

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
        long travelId = travelService.createTravel(travelRequest, member.getId());
        TravelMember travelMember = travelMemberRepostiory.findAll().get(0);

        // then
        assertAll(
                () -> Assertions.assertThat(travelMember.getMember().getId()).isEqualTo(member.getId()),
                () -> Assertions.assertThat(travelMember.getTravel().getId()).isEqualTo(travelId)
        );
    }
}
