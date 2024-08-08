package com.staccato.travel.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;

import com.staccato.ServiceSliceTest;
import com.staccato.exception.ForbiddenException;
import com.staccato.exception.StaccatoException;
import com.staccato.member.domain.Member;
import com.staccato.member.repository.MemberRepository;
import com.staccato.travel.domain.Travel;
import com.staccato.travel.domain.TravelMember;
import com.staccato.travel.repository.TravelMemberRepository;
import com.staccato.travel.repository.TravelRepository;
import com.staccato.travel.service.dto.request.TravelRequest;
import com.staccato.travel.service.dto.response.TravelDetailResponse;
import com.staccato.travel.service.dto.response.TravelIdResponse;
import com.staccato.travel.service.dto.response.TravelResponses;
import com.staccato.travel.service.dto.response.VisitResponse;
import com.staccato.visit.domain.Visit;
import com.staccato.visit.fixture.VisitFixture;
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

    static Stream<Arguments> updateTravelProvider() {
        return Stream.of(
                Arguments.of(
                        new TravelRequest("imageUrl", "2024 여름 휴가다!", "친한 친구들과 함께한 여름 휴가 여행", LocalDate.of(2024, 8, 1), LocalDate.of(2024, 8, 10)),
                        new MockMultipartFile("travelThumbnailUrl", "example.jpg".getBytes()), "fakeUrl"),
                Arguments.of(
                        new TravelRequest(null, "2024 여름 휴가다!", "친한 친구들과 함께한 여름 휴가 여행", LocalDate.of(2024, 8, 1), LocalDate.of(2024, 8, 10)),
                        null, null),
                Arguments.of(
                        new TravelRequest("imageUrl", "2024 여름 휴가다!", "친한 친구들과 함께한 여름 휴가 여행", LocalDate.of(2024, 8, 1), LocalDate.of(2024, 8, 10)),
                        null, "imageUrl"));
    }

    @DisplayName("여행 상세 정보를 기반으로, 여행 상세를 생성하고 작성자를 저장한다.")
    @Test
    void createTravel() {
        // given
        TravelRequest travelRequest = createTravelRequest(2024);
        Member member = saveMember();

        // when
        TravelIdResponse travelIdResponse = travelService.createTravel(travelRequest, null, member);
        TravelMember travelMember = travelMemberRepository.findAllByMemberIdOrderByTravelStartAtDesc(member.getId()).get(0);

        // then
        assertAll(
                () -> assertThat(travelMember.getMember().getId()).isEqualTo(member.getId()),
                () -> assertThat(travelMember.getTravel().getId()).isEqualTo(travelIdResponse.travelId())
        );
    }

    @DisplayName("조건에 따라 여행 상세 목록을 조회한다.")
    @MethodSource("yearProvider")
    @ParameterizedTest
    void readAllTravels(Integer year, int expectedSize) {
        // given
        Member member = saveMember();
        travelService.createTravel(createTravelRequest(2023), null, member);
        travelService.createTravel(createTravelRequest(2024), null, member);

        // when
        TravelResponses travelResponses = travelService.readAllTravels(member, year);

        // then
        assertThat(travelResponses.travels()).hasSize(expectedSize);
    }

    @DisplayName("특정 여행 상세를 조회한다.")
    @Test
    void readTravelById() {
        // given
        Member member = saveMember();

        TravelIdResponse travelIdResponse = travelService.createTravel(createTravelRequest(2023), null, member);

        // when
        TravelDetailResponse travelDetailResponse = travelService.readTravelById(travelIdResponse.travelId(), member);

        // then
        assertAll(
                () -> assertThat(travelDetailResponse.travelId()).isEqualTo(travelIdResponse.travelId()),
                () -> assertThat(travelDetailResponse.mates()).hasSize(1)
        );
    }

    @DisplayName("본인 것이 아닌 특정 여행 상세를 조회하려고 하면 예외가 발생한다.")
    @Test
    void cannotReadTravelByIdIfNotOwner() {
        // given
        Member member = saveMember();
        Member otherMember = saveMember();

        TravelIdResponse travelIdResponse = travelService.createTravel(createTravelRequest(2023), null, member);

        // when & then
        assertThatThrownBy(() -> travelService.readTravelById(travelIdResponse.travelId(), otherMember))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("요청하신 작업을 처리할 권한이 없습니다.");
    }

    @DisplayName("특정 여행 상세를 조회하면 방문 기록은 오래된 순으로 반환한다.")
    @Test
    void readTravelByIdOrderByVisitedAt() {
        // given
        Member member = saveMember();

        TravelIdResponse travelIdResponse = travelService.createTravel(createTravelRequest(2023), null, member);
        Visit firstVisit = saveVisit(LocalDateTime.of(2023, 7, 1, 10, 0), travelIdResponse.travelId());
        Visit secondVisit = saveVisit(LocalDateTime.of(2023, 7, 1, 10, 10), travelIdResponse.travelId());
        Visit lastVisit = saveVisit(LocalDateTime.of(2023, 7, 5, 9, 0), travelIdResponse.travelId());

        // when
        TravelDetailResponse travelDetailResponse = travelService.readTravelById(travelIdResponse.travelId(), member);

        // then
        assertAll(
                () -> assertThat(travelDetailResponse.travelId()).isEqualTo(travelIdResponse.travelId()),
                () -> assertThat(travelDetailResponse.visits()).hasSize(3),
                () -> assertThat(travelDetailResponse.visits().stream().map(VisitResponse::visitId).toList())
                        .containsExactly(firstVisit.getId(), secondVisit.getId(), lastVisit.getId())
        );
    }

    private Visit saveVisit(LocalDateTime visitedAt, long travelId) {
        return visitRepository.save(VisitFixture.create(travelRepository.findById(travelId).get(), visitedAt));
    }

    @DisplayName("존재하지 않는 여행 상세를 조회하려고 할 경우 예외가 발생한다.")
    @Test
    void failReadTravel() {
        // given
        Member member = saveMember();
        long unknownId = 1;

        // when & then
        assertThatThrownBy(() -> travelService.readTravelById(unknownId, member))
                .isInstanceOf(StaccatoException.class)
                .hasMessage("요청하신 여행을 찾을 수 없어요.");
    }

    @DisplayName("여행 상세 정보를 기반으로, 여행 상세를 수정한다.")
    @MethodSource("updateTravelProvider")
    @ParameterizedTest
    void updateTravel(TravelRequest updatedTravel, MockMultipartFile updatedFile, String expected) {
        // given
        Member member = saveMember();
        MockMultipartFile file = new MockMultipartFile("travelThumbnailUrl", "example.jpg".getBytes());
        TravelIdResponse travelResponse = travelService.createTravel(createTravelRequest(2024), file, member);

        // when
        travelService.updateTravel(updatedTravel, travelResponse.travelId(), updatedFile, member);
        Travel foundedTravel = travelRepository.findById(travelResponse.travelId()).get();

        // then
        assertAll(
                () -> assertThat(foundedTravel.getId()).isEqualTo(travelResponse.travelId()),
                () -> assertThat(foundedTravel.getTitle()).isEqualTo(updatedTravel.travelTitle()),
                () -> assertThat(foundedTravel.getDescription()).isEqualTo(updatedTravel.description()),
                () -> assertThat(foundedTravel.getStartAt()).isEqualTo(updatedTravel.startAt()),
                () -> assertThat(foundedTravel.getEndAt()).isEqualTo(updatedTravel.endAt()),
                () -> assertThat(foundedTravel.getThumbnailUrl()).isEqualTo(expected)
        );
    }

    @DisplayName("존재하지 않는 여행 상세를 수정하려 할 경우 예외가 발생한다.")
    @Test
    void failUpdateTravel() {
        // given
        Member member = saveMember();
        TravelRequest travelRequest = createTravelRequest(2023);
        MockMultipartFile file = new MockMultipartFile("travelThumbnailUrl", "example.jpg".getBytes());

        // when & then
        assertThatThrownBy(() -> travelService.updateTravel(travelRequest, 1L, file, member))
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

    @DisplayName("본인 것이 아닌 여행 상세를 수정하려고 하면 예외가 발생한다.")
    @Test
    void cannotUpdateTravelIfNotOwner() {
        // given
        Member member = saveMember();
        Member otherMember = saveMember();
        TravelRequest updatedTravel = createTravelRequest(2023);
        TravelIdResponse travelIdResponse = travelService.createTravel(createTravelRequest(2023), null, member);
        MockMultipartFile file = new MockMultipartFile("travelThumbnailUrl", "example.jpg".getBytes());

        // when & then
        assertThatThrownBy(() -> travelService.updateTravel(updatedTravel, travelIdResponse.travelId(), file, otherMember))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("요청하신 작업을 처리할 권한이 없습니다.");
    }

    @DisplayName("여행 식별값을 통해 여행 상세를 삭제한다.")
    @Test
    void deleteTravel() {
        // given
        Member member = saveMember();
        TravelIdResponse travelIdResponse = travelService.createTravel(createTravelRequest(2023), null, member);

        // when
        travelService.deleteTravel(travelIdResponse.travelId(), member);

        // then
        assertAll(
                () -> assertThat(travelRepository.findById(travelIdResponse.travelId())).isEmpty(),
                () -> assertThat(travelMemberRepository.findAll()).hasSize(0)
        );
    }

    @DisplayName("본인 것이 아닌 여행 상세를 삭제하려고 하면 예외가 발생한다.")
    @Test
    void cannotDeleteTravelIfNotOwner() {
        // given
        Member member = saveMember();
        Member otherMember = saveMember();
        TravelIdResponse travelIdResponse = travelService.createTravel(createTravelRequest(2023), null, member);

        // when & then
        assertThatThrownBy(() -> travelService.deleteTravel(travelIdResponse.travelId(), otherMember))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("요청하신 작업을 처리할 권한이 없습니다.");
    }

    @DisplayName("방문기록이 존재하는 여행 상세에 삭제를 시도할 경우 예외가 발생한다.")
    @Test
    void failDeleteTravelByExistingVisits() {
        // given
        Member member = saveMember();
        TravelIdResponse travelIdResponse = travelService.createTravel(createTravelRequest(2023), null, member);
        saveVisit(LocalDateTime.of(2024, 7, 10, 10, 0), travelIdResponse.travelId());

        // when & then
        assertThatThrownBy(() -> travelService.deleteTravel(travelIdResponse.travelId(), member))
                .isInstanceOf(StaccatoException.class)
                .hasMessage("해당 여행 상세에 방문 기록이 남아있어 삭제할 수 없습니다.");
    }

    private Member saveMember() {
        return memberRepository.save(Member.builder().nickname("staccato").build());
    }
}
