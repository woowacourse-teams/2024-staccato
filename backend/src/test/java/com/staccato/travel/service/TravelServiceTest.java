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
import com.staccato.travel.repository.TravelMemberRepository;
import com.staccato.travel.service.dto.request.TravelRequest;
import com.staccato.travel.service.dto.response.TravelDetailResponses;
import com.staccato.travel.service.dto.response.TravelResponse;

class TravelServiceTest extends ServiceSliceTest {
    @Autowired
    private TravelService travelService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private TravelMemberRepository travelMemberRepository;

    @DisplayName("여행 상세 정보를 기반으로, 여행 상세를 생성하고 작성자를 저장한다.")
    @Test
    void createTravel() {
        // given
        TravelRequest travelRequest = createTravelRequest();
        Member member = memberRepository.save(Member.builder().nickname("staccato").build());

        // when
        TravelResponse travel = travelService.createTravel(travelRequest, member.getId());
        TravelMember travelMember = travelMemberRepository.findAll().get(0);

        // then
        assertAll(
                () -> Assertions.assertThat(travelMember.getMember().getId()).isEqualTo(member.getId()),
                () -> Assertions.assertThat(travelMember.getTravel().getId()).isEqualTo(travel.travelId())
        );
    }

    @DisplayName("모든 여행 상세 목록을 조회한다.")
    @Test
    void readAllTravels() {
        // given
        TravelRequest travelRequest = createTravelRequest();
        Member member = memberRepository.save(Member.builder().nickname("staccato").build());
        travelService.createTravel(travelRequest, member.getId());

        // when
        TravelDetailResponses travelDetailResponses = travelService.readAllTravels(member.getId());

        // then
        assertAll(
                () -> Assertions.assertThat(travelDetailResponses.travels()).hasSize(1),
                () -> Assertions.assertThat(travelDetailResponses.travels().get(0).mates().members()).hasSize(1)
        );
    }

    private static TravelRequest createTravelRequest() {
        return new TravelRequest(
                "https://example.com/travels/geumohrm.jpg",
                "2023 여름 휴가",
                "친구들과 함께한 여름 휴가 여행",
                LocalDate.of(2023, 7, 1),
                LocalDate.of(2023, 7, 10)
        );
    }
}
