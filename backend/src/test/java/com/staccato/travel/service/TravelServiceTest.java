package com.staccato.travel.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.time.LocalDate;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

import com.staccato.ServiceSliceTest;
import com.staccato.exception.StaccatoException;
import com.staccato.member.domain.Member;
import com.staccato.member.repository.MemberRepository;
import com.staccato.travel.domain.Travel;
import com.staccato.travel.domain.TravelMember;
import com.staccato.travel.repository.TravelMemberRepository;
import com.staccato.travel.repository.TravelRepository;
import com.staccato.travel.service.dto.request.TravelRequest;
import com.staccato.travel.service.dto.response.TravelResponses;

class TravelServiceTest extends ServiceSliceTest {
    @Autowired
    private TravelService travelService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private TravelMemberRepository travelMemberRepository;
    @Autowired
    private TravelRepository travelRepository;

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
        long travelId = travelService.createTravel(travelRequest, member.getId());
        TravelMember travelMember = travelMemberRepository.findAll().get(0);

        // then
        assertAll(
                () -> assertThat(travelMember.getMember().getId()).isEqualTo(member.getId()),
                () -> assertThat(travelMember.getTravel().getId()).isEqualTo(travelId)
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
        TravelResponses travelResponses = travelService.readAllTravels(member.getId(), year);

        // then
        assertThat(travelResponses.travels()).hasSize(expectedSize);
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

    @DisplayName("여행 상세 정보를 기반으로, 여행 상세를 수정한다.")
    @Test
    void updateTravel() {
        // given
        Long travelId = 1L;
        Travel originTravel = new Travel(
                "https://example.com/travels/geumohrm.jpg",
                "2023 여름 휴가",
                "친구들과 함께한 여름 휴가 여행",
                LocalDate.of(2023, 7, 1),
                LocalDate.of(2023, 7, 10)
        );
        travelRepository.save(originTravel);
        TravelRequest updatedTravel = new TravelRequest(
                "https://example.com/travels/geumohrm.jpg",
                "2023 신나는 여름 휴가",
                "친한 친구들과 함께한 여름 휴가 여행",
                LocalDate.of(2023, 7, 1),
                LocalDate.of(2023, 7, 10)
        );

        // when
        travelService.updateTravel(updatedTravel, travelId);
        Travel travel = travelRepository.findById(travelId).get();

        // then
        assertAll(
                () -> assertThat(travel.getId()).isEqualTo(travelId),
                () -> assertThat(travel.getTitle()).isEqualTo(updatedTravel.travelTitle()),
                () -> assertThat(travel.getDescription()).isEqualTo(updatedTravel.description()),
                () -> assertThat(travel.getStartAt()).isEqualTo(updatedTravel.startAt()),
                () -> assertThat(travel.getEndAt()).isEqualTo(updatedTravel.endAt())
        );
    }

    @DisplayName("존재하지 않는 여행 상세를 수정하려 할 경우 예외가 발생한다.")
    @Test
    void failUpdateTravel() {
        // given
        TravelRequest travelRequest = new TravelRequest(
                "https://example.com/travels/geumohrm.jpg",
                "2023 여름 휴가",
                "친구들과 함께한 여름 휴가 여행",
                LocalDate.of(2023, 7, 1),
                LocalDate.of(2023, 7, 10)
        );

        // when & then
        assertThatThrownBy(() -> travelService.updateTravel(travelRequest, 1L))
                .isInstanceOf(StaccatoException.class)
                .hasMessage("요청하신 여행을 찾을 수 없어요.");
    }
}
