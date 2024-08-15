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
import com.staccato.travel.service.dto.request.MemoryRequest;
import com.staccato.travel.service.dto.response.MemoryDetailResponse;
import com.staccato.travel.service.dto.response.MemoryIdResponse;
import com.staccato.travel.service.dto.response.MemoryResponses;
import com.staccato.travel.service.dto.response.MomentResponse;
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
                        new MemoryRequest("imageUrl", "2024 여름 휴가다!", "친한 친구들과 함께한 여름 휴가 여행", LocalDate.of(2024, 8, 1), LocalDate.of(2024, 8, 10)),
                        new MockMultipartFile("memoryThumbnailUrl", "example.jpg".getBytes()), "fakeUrl"),
                Arguments.of(
                        new MemoryRequest(null, "2024 여름 휴가다!", "친한 친구들과 함께한 여름 휴가 여행", LocalDate.of(2024, 8, 1), LocalDate.of(2024, 8, 10)),
                        null, null),
                Arguments.of(
                        new MemoryRequest("imageUrl", "2024 여름 휴가다!", "친한 친구들과 함께한 여름 휴가 여행", LocalDate.of(2024, 8, 1), LocalDate.of(2024, 8, 10)),
                        null, "imageUrl"));
    }

    @DisplayName("여행 상세 정보를 기반으로, 여행 상세를 생성하고 작성자를 저장한다.")
    @Test
    void createTravel() {
        // given
        MemoryRequest memoryRequest = createTravelRequest(2024);
        Member member = saveMember();

        // when
        MemoryIdResponse memoryIdResponse = travelService.createTravel(memoryRequest, null, member);
        TravelMember travelMember = travelMemberRepository.findAllByMemberIdOrderByTravelStartAtDesc(member.getId()).get(0);

        // then
        assertAll(
                () -> assertThat(travelMember.getMember().getId()).isEqualTo(member.getId()),
                () -> assertThat(travelMember.getTravel().getId()).isEqualTo(memoryIdResponse.memoryId())
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
        MemoryResponses memoryResponses = travelService.readAllTravels(member, year);

        // then
        assertThat(memoryResponses.memories()).hasSize(expectedSize);
    }

    @DisplayName("특정 여행 상세를 조회한다.")
    @Test
    void readTravelById() {
        // given
        Member member = saveMember();

        MemoryIdResponse memoryIdResponse = travelService.createTravel(createTravelRequest(2023), null, member);

        // when
        MemoryDetailResponse memoryDetailResponse = travelService.readTravelById(memoryIdResponse.memoryId(), member);

        // then
        assertAll(
                () -> assertThat(memoryDetailResponse.memoryId()).isEqualTo(memoryIdResponse.memoryId()),
                () -> assertThat(memoryDetailResponse.mates()).hasSize(1)
        );
    }

    @DisplayName("본인 것이 아닌 특정 여행 상세를 조회하려고 하면 예외가 발생한다.")
    @Test
    void cannotReadTravelByIdIfNotOwner() {
        // given
        Member member = saveMember();
        Member otherMember = saveMember();

        MemoryIdResponse memoryIdResponse = travelService.createTravel(createTravelRequest(2023), null, member);

        // when & then
        assertThatThrownBy(() -> travelService.readTravelById(memoryIdResponse.memoryId(), otherMember))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("요청하신 작업을 처리할 권한이 없습니다.");
    }

    @DisplayName("특정 여행 상세를 조회하면 방문 기록은 오래된 순으로 반환한다.")
    @Test
    void readTravelByIdOrderByVisitedAt() {
        // given
        Member member = saveMember();

        MemoryIdResponse memoryIdResponse = travelService.createTravel(createTravelRequest(2023), null, member);
        Visit firstVisit = saveVisit(LocalDateTime.of(2023, 7, 1, 10, 0), memoryIdResponse.memoryId());
        Visit secondVisit = saveVisit(LocalDateTime.of(2023, 7, 1, 10, 10), memoryIdResponse.memoryId());
        Visit lastVisit = saveVisit(LocalDateTime.of(2023, 7, 5, 9, 0), memoryIdResponse.memoryId());

        // when
        MemoryDetailResponse memoryDetailResponse = travelService.readTravelById(memoryIdResponse.memoryId(), member);

        // then
        assertAll(
                () -> assertThat(memoryDetailResponse.memoryId()).isEqualTo(memoryIdResponse.memoryId()),
                () -> assertThat(memoryDetailResponse.moments()).hasSize(3),
                () -> assertThat(memoryDetailResponse.moments().stream().map(MomentResponse::momentId).toList())
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
    void updateTravel(MemoryRequest updatedTravel, MockMultipartFile updatedFile, String expected) {
        // given
        Member member = saveMember();
        MockMultipartFile file = new MockMultipartFile("memoryThumbnailUrl", "example.jpg".getBytes());
        MemoryIdResponse travelResponse = travelService.createTravel(createTravelRequest(2024), file, member);

        // when
        travelService.updateTravel(updatedTravel, travelResponse.memoryId(), updatedFile, member);
        Travel foundedTravel = travelRepository.findById(travelResponse.memoryId()).get();

        // then
        assertAll(
                () -> assertThat(foundedTravel.getId()).isEqualTo(travelResponse.memoryId()),
                () -> assertThat(foundedTravel.getTitle()).isEqualTo(updatedTravel.memoryTitle()),
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
        MemoryRequest memoryRequest = createTravelRequest(2023);
        MockMultipartFile file = new MockMultipartFile("memoryThumbnailUrl", "example.jpg".getBytes());

        // when & then
        assertThatThrownBy(() -> travelService.updateTravel(memoryRequest, 1L, file, member))
                .isInstanceOf(StaccatoException.class)
                .hasMessage("요청하신 여행을 찾을 수 없어요.");
    }

    private MemoryRequest createTravelRequest(int year) {
        return new MemoryRequest(
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
        MemoryRequest updatedTravel = createTravelRequest(2023);
        MemoryIdResponse memoryIdResponse = travelService.createTravel(createTravelRequest(2023), null, member);
        MockMultipartFile file = new MockMultipartFile("memoryThumbnailUrl", "example.jpg".getBytes());

        // when & then
        assertThatThrownBy(() -> travelService.updateTravel(updatedTravel, memoryIdResponse.memoryId(), file, otherMember))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("요청하신 작업을 처리할 권한이 없습니다.");
    }

    @DisplayName("여행 식별값을 통해 여행 상세를 삭제한다.")
    @Test
    void deleteTravel() {
        // given
        Member member = saveMember();
        MemoryIdResponse memoryIdResponse = travelService.createTravel(createTravelRequest(2023), null, member);

        // when
        travelService.deleteTravel(memoryIdResponse.memoryId(), member);

        // then
        assertAll(
                () -> assertThat(travelRepository.findById(memoryIdResponse.memoryId())).isEmpty(),
                () -> assertThat(travelMemberRepository.findAll()).hasSize(0)
        );
    }

    @DisplayName("본인 것이 아닌 여행 상세를 삭제하려고 하면 예외가 발생한다.")
    @Test
    void cannotDeleteTravelIfNotOwner() {
        // given
        Member member = saveMember();
        Member otherMember = saveMember();
        MemoryIdResponse memoryIdResponse = travelService.createTravel(createTravelRequest(2023), null, member);

        // when & then
        assertThatThrownBy(() -> travelService.deleteTravel(memoryIdResponse.memoryId(), otherMember))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("요청하신 작업을 처리할 권한이 없습니다.");
    }

    @DisplayName("방문기록이 존재하는 여행 상세에 삭제를 시도할 경우 예외가 발생한다.")
    @Test
    void failDeleteTravelByExistingVisits() {
        // given
        Member member = saveMember();
        MemoryIdResponse memoryIdResponse = travelService.createTravel(createTravelRequest(2023), null, member);
        saveVisit(LocalDateTime.of(2024, 7, 10, 10, 0), memoryIdResponse.memoryId());

        // when & then
        assertThatThrownBy(() -> travelService.deleteTravel(memoryIdResponse.memoryId(), member))
                .isInstanceOf(StaccatoException.class)
                .hasMessage("해당 여행 상세에 방문 기록이 남아있어 삭제할 수 없습니다.");
    }

    private Member saveMember() {
        return memberRepository.save(Member.builder().nickname("staccato").build());
    }
}
