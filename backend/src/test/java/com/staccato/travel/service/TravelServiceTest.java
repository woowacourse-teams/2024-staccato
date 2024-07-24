package com.staccato.travel.service;

import static org.junit.jupiter.api.Assertions.assertAll;

import java.time.LocalDate;
import java.util.stream.Stream;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
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

    static Stream<Arguments> yearProvider() {
        return Stream.of(
                Arguments.of(null, 2),
                Arguments.of(2023, 1)
        );
    }

    @DisplayName("여행 상세 정보를 기반으로, 여행 상세를 생성하고 작성자를 저장한다.")
    @Test
    void createTravel() {
        // given
        TravelRequest travelRequest = createTravelRequest(2024);
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

    @DisplayName("조건에 따라 여행 상세 목록을 조회한다.")
    @MethodSource("yearProvider")
    @ParameterizedTest
    void readAllTravels(Integer year, int expectedSize) {
        // given
        Member member = memberRepository.save(Member.builder().nickname("staccato").build());
        travelService.createTravel(createTravelRequest(2023), member.getId());
        travelService.createTravel(createTravelRequest(2024), member.getId());

        // when
        TravelDetailResponses travelDetailResponses = travelService.readAllTravels(member.getId(), year);

        // then
        Assertions.assertThat(travelDetailResponses.travels()).hasSize(expectedSize);
    }

    private static TravelRequest createTravelRequest(int year) {
        return new TravelRequest(
                "https://example.com/travels/geumohrm.jpg",
                year + " 여름 휴가",
                "친구들과 함께한 여름 휴가 여행",
                LocalDate.of(year, 7, 1),
                LocalDate.of(2024, 7, 10)
        );
    }
}
