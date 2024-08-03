package com.staccato.travel.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;
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
import com.staccato.travel.service.dto.response.TravelDetailResponse;
import com.staccato.travel.service.dto.response.TravelResponses;
import com.staccato.travel.service.dto.response.VisitResponse;
import com.staccato.visit.domain.Visit;
import com.staccato.visit.repository.VisitRepository;

class TravelServiceTest extends ServiceSliceTest {
    @Autowired
    private TravelService travelService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private TravelMemberRepository travelMemberRepository;
    @Autowired
    private TravelRepository travelRepository;
    @Autowired
    private VisitRepository visitRepository;

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
        Member member = saveMember();

        // when
        long travelId = travelService.createTravel(travelRequest, member.getId());
        TravelMember travelMember = travelMemberRepository.findAllByMemberIdOrderByTravelStartAtDesc(member.getId()).get(0);

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
        Member member = saveMember();
        travelService.createTravel(createTravelRequest(2023), member.getId());
        travelService.createTravel(createTravelRequest(2024), member.getId());

        // when
        TravelResponses travelResponses = travelService.readAllTravels(member.getId(), year);

        // then
        assertThat(travelResponses.travels()).hasSize(expectedSize);
    }

    @DisplayName("특정 여행 상세를 조회한다.")
    @Test
    void readTravelById() {
        // given
        Member member = saveMember();

        long targetId = travelService.createTravel(createTravelRequest(2023), member.getId());
        Visit visit = saveVisit(LocalDate.of(2023, 7, 1), targetId);

        long otherId = travelService.createTravel(createTravelRequest(2023), member.getId());
        saveVisit(LocalDate.of(2023, 7, 1), otherId);

        // when
        TravelDetailResponse travelDetailResponse = travelService.readTravelById(targetId);

        // then
        assertAll(
                () -> assertThat(travelDetailResponse.travelId()).isEqualTo(targetId),
                () -> assertThat(travelDetailResponse.mates()).hasSize(1),
                () -> assertThat(travelDetailResponse.visits()).hasSize(1),
                () -> assertThat(travelDetailResponse.visits().get(0).visitId()).isEqualTo(visit.getId())
        );
    }

    @DisplayName("특정 여행 상세를 조회하면 방문 기록은 오래된 순으로 반환한다.")
    @Test
    void readTravelByIdOrderByVisitedAt() {
        // given
        Member member = saveMember();

        long visitId = travelService.createTravel(createTravelRequest(2023), member.getId());
        Visit visit = saveVisit(LocalDate.of(2023, 7, 1), visitId);
        Visit nextVisit = saveVisit(LocalDate.of(2023, 7, 5), visitId);

        // when
        TravelDetailResponse travelDetailResponse = travelService.readTravelById(visitId);

        // then
        assertAll(
                () -> assertThat(travelDetailResponse.travelId()).isEqualTo(visitId),
                () -> assertThat(travelDetailResponse.visits()).hasSize(2),
                () -> assertThat(travelDetailResponse.visits().stream().map(VisitResponse::visitedAt).toList())
                        .containsExactly(visit.getVisitedAt(), nextVisit.getVisitedAt())
        );
    }

    private Visit saveVisit(LocalDate visitedAt, long travelId) {
        return visitRepository.save(
                Visit.builder()
                        .visitedAt(visitedAt)
                        .placeName("placeName")
                        .latitude(BigDecimal.ONE)
                        .longitude(BigDecimal.ONE)
                        .address("address")
                        .travel(travelRepository.findById(travelId).get())
                        .build());
    }

    @DisplayName("여행 상세 정보를 기반으로, 여행 상세를 수정한다.")
    @Test
    void updateTravel() {
        // given
        Member member = saveMember();
        Long travelId = travelService.createTravel(createTravelRequest(2023), member.getId());
        TravelRequest updatedTravel = new TravelRequest(
                "https://example.com/travels/geumohrm.jpg",
                "2023 신나는 여름 휴가",
                "친한 친구들과 함께한 여름 휴가 여행",
                LocalDate.of(2023, 7, 1),
                LocalDate.of(2023, 7, 10)
        );

        // when
        travelService.updateTravel(updatedTravel, travelId);
        Travel foundedTravel = travelRepository.findById(travelId).get();

        // then
        assertAll(
                () -> assertThat(foundedTravel.getId()).isEqualTo(travelId),
                () -> assertThat(foundedTravel.getTitle()).isEqualTo(updatedTravel.travelTitle()),
                () -> assertThat(foundedTravel.getDescription()).isEqualTo(updatedTravel.description()),
                () -> assertThat(foundedTravel.getStartAt()).isEqualTo(updatedTravel.startAt()),
                () -> assertThat(foundedTravel.getEndAt()).isEqualTo(updatedTravel.endAt())
        );
    }

    @DisplayName("존재하지 않는 여행 상세를 수정하려 할 경우 예외가 발생한다.")
    @Test
    void failUpdateTravel() {
        // given
        TravelRequest travelRequest = createTravelRequest(2023);

        // when & then
        assertThatThrownBy(() -> travelService.updateTravel(travelRequest, 1L))
                .isInstanceOf(StaccatoException.class)
                .hasMessage("요청하신 여행을 찾을 수 없어요.");
    }

    private TravelRequest createTravelRequest(int year) {
        return new TravelRequest(
                "https://example.com/travels/geumohrm.jpg",
                year + " 여름 휴가",
                "친구들과 함께한 여름 휴가 여행",
                LocalDate.of(year, 7, 1),
                LocalDate.of(2024, 7, 10)
        );
    }

    @DisplayName("여행 식별값을 통해 여행 상세를 삭제한다.")
    @Test
    void deleteTravel() {
        // given
        Member member = saveMember();
        Long travelId = travelService.createTravel(createTravelRequest(2023), member.getId());

        // when
        travelService.deleteTravel(travelId);

        // then
        assertAll(
                () -> assertThat(travelRepository.findById(travelId)).isEmpty(),
                () -> assertThat(travelMemberRepository.findAll()).hasSize(0)
        );
    }

    @DisplayName("방문기록이 존재하는 여행 상세에 삭제를 시도할 경우 예외가 발생한다.")
    @Test
    void failDeleteTravel() {
        // given
        Member member = saveMember();
        Long travelId = travelService.createTravel(createTravelRequest(2023), member.getId());
        Travel foundTravel = travelRepository.findById(travelId).get();
        visitRepository.save(Visit.builder()
                .visitedAt(LocalDate.of(2024, 7, 10))
                .placeName("placeName")
                .latitude(BigDecimal.ONE)
                .longitude(BigDecimal.ONE)
                .address("address")
                .travel(foundTravel)
                .build());

        // when & then
        assertThatThrownBy(() -> travelService.deleteTravel(foundTravel.getId()))
                .isInstanceOf(StaccatoException.class)
                .hasMessage("해당 여행 상세에 방문 기록이 남아있어 삭제할 수 없습니다.");
    }

    private Member saveMember() {
        return memberRepository.save(Member.builder().nickname("staccato").build());
    }

    @DisplayName("존재하지 않는 여행 상세를 조회하려고 할 경우 예외가 발생한다.")
    @Test
    void failReadTravel() {
        // given
        long unknownId = 1;

        // when & then
        assertThatThrownBy(() -> travelService.readTravelById(unknownId))
                .isInstanceOf(StaccatoException.class)
                .hasMessage("요청하신 여행을 찾을 수 없어요.");
    }
}
