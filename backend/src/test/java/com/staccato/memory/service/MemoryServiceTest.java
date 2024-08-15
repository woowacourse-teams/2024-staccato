package com.staccato.memory.service;

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
import com.staccato.memory.domain.Memory;
import com.staccato.memory.domain.MemoryMember;
import com.staccato.memory.repository.MemoryMemberRepository;
import com.staccato.memory.repository.MemoryRepository;
import com.staccato.memory.service.dto.request.MemoryRequest;
import com.staccato.memory.service.dto.response.MemoryDetailResponse;
import com.staccato.memory.service.dto.response.MemoryIdResponse;
import com.staccato.memory.service.dto.response.MemoryResponses;
import com.staccato.memory.service.dto.response.MomentResponse;
import com.staccato.moment.domain.Moment;
import com.staccato.moment.fixture.MomentFixture;
import com.staccato.moment.repository.MomentRepository;

class MemoryServiceTest extends ServiceSliceTest {
    @Autowired
    private MemoryService memoryService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MemoryMemberRepository memoryMemberRepository;
    @Autowired
    private MemoryRepository memoryRepository;
    @Autowired
    private MomentRepository momentRepository;

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
        MemoryIdResponse memoryIdResponse = memoryService.createMemory(memoryRequest, null, member);
        MemoryMember memoryMember = memoryMemberRepository.findAllByMemberIdOrderByMemoryStartAtDesc(member.getId()).get(0);

        // then
        assertAll(
                () -> assertThat(memoryMember.getMember().getId()).isEqualTo(member.getId()),
                () -> assertThat(memoryMember.getMemory().getId()).isEqualTo(memoryIdResponse.memoryId())
        );
    }

    @DisplayName("조건에 따라 여행 상세 목록을 조회한다.")
    @MethodSource("yearProvider")
    @ParameterizedTest
    void readAllTravels(Integer year, int expectedSize) {
        // given
        Member member = saveMember();
        memoryService.createMemory(createTravelRequest(2023), null, member);
        memoryService.createMemory(createTravelRequest(2024), null, member);

        // when
        MemoryResponses memoryResponses = memoryService.readAllMemories(member, year);

        // then
        assertThat(memoryResponses.memories()).hasSize(expectedSize);
    }

    @DisplayName("특정 여행 상세를 조회한다.")
    @Test
    void readTravelById() {
        // given
        Member member = saveMember();

        MemoryIdResponse memoryIdResponse = memoryService.createMemory(createTravelRequest(2023), null, member);

        // when
        MemoryDetailResponse memoryDetailResponse = memoryService.readMemoryById(memoryIdResponse.memoryId(), member);

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

        MemoryIdResponse memoryIdResponse = memoryService.createMemory(createTravelRequest(2023), null, member);

        // when & then
        assertThatThrownBy(() -> memoryService.readMemoryById(memoryIdResponse.memoryId(), otherMember))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("요청하신 작업을 처리할 권한이 없습니다.");
    }

    @DisplayName("특정 여행 상세를 조회하면 방문 기록은 오래된 순으로 반환한다.")
    @Test
    void readTravelByIdOrderByVisitedAt() {
        // given
        Member member = saveMember();

        MemoryIdResponse memoryIdResponse = memoryService.createMemory(createTravelRequest(2023), null, member);
        Moment firstMoment = saveVisit(LocalDateTime.of(2023, 7, 1, 10, 0), memoryIdResponse.memoryId());
        Moment secondMoment = saveVisit(LocalDateTime.of(2023, 7, 1, 10, 10), memoryIdResponse.memoryId());
        Moment lastMoment = saveVisit(LocalDateTime.of(2023, 7, 5, 9, 0), memoryIdResponse.memoryId());

        // when
        MemoryDetailResponse memoryDetailResponse = memoryService.readMemoryById(memoryIdResponse.memoryId(), member);

        // then
        assertAll(
                () -> assertThat(memoryDetailResponse.memoryId()).isEqualTo(memoryIdResponse.memoryId()),
                () -> assertThat(memoryDetailResponse.moments()).hasSize(3),
                () -> assertThat(memoryDetailResponse.moments().stream().map(MomentResponse::momentId).toList())
                        .containsExactly(firstMoment.getId(), secondMoment.getId(), lastMoment.getId())
        );
    }

    private Moment saveVisit(LocalDateTime visitedAt, long travelId) {
        return momentRepository.save(MomentFixture.create(memoryRepository.findById(travelId).get(), visitedAt));
    }

    @DisplayName("존재하지 않는 여행 상세를 조회하려고 할 경우 예외가 발생한다.")
    @Test
    void failReadTravel() {
        // given
        Member member = saveMember();
        long unknownId = 1;

        // when & then
        assertThatThrownBy(() -> memoryService.readMemoryById(unknownId, member))
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
        MemoryIdResponse travelResponse = memoryService.createMemory(createTravelRequest(2024), file, member);

        // when
        memoryService.updateMemory(updatedTravel, travelResponse.memoryId(), updatedFile, member);
        Memory foundedMemory = memoryRepository.findById(travelResponse.memoryId()).get();

        // then
        assertAll(
                () -> assertThat(foundedMemory.getId()).isEqualTo(travelResponse.memoryId()),
                () -> assertThat(foundedMemory.getTitle()).isEqualTo(updatedTravel.memoryTitle()),
                () -> assertThat(foundedMemory.getDescription()).isEqualTo(updatedTravel.description()),
                () -> assertThat(foundedMemory.getStartAt()).isEqualTo(updatedTravel.startAt()),
                () -> assertThat(foundedMemory.getEndAt()).isEqualTo(updatedTravel.endAt()),
                () -> assertThat(foundedMemory.getThumbnailUrl()).isEqualTo(expected)
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
        assertThatThrownBy(() -> memoryService.updateMemory(memoryRequest, 1L, file, member))
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
        MemoryIdResponse memoryIdResponse = memoryService.createMemory(createTravelRequest(2023), null, member);
        MockMultipartFile file = new MockMultipartFile("memoryThumbnailUrl", "example.jpg".getBytes());

        // when & then
        assertThatThrownBy(() -> memoryService.updateMemory(updatedTravel, memoryIdResponse.memoryId(), file, otherMember))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("요청하신 작업을 처리할 권한이 없습니다.");
    }

    @DisplayName("여행 식별값을 통해 여행 상세를 삭제한다.")
    @Test
    void deleteTravel() {
        // given
        Member member = saveMember();
        MemoryIdResponse memoryIdResponse = memoryService.createMemory(createTravelRequest(2023), null, member);

        // when
        memoryService.deleteMemory(memoryIdResponse.memoryId(), member);

        // then
        assertAll(
                () -> assertThat(memoryRepository.findById(memoryIdResponse.memoryId())).isEmpty(),
                () -> assertThat(memoryMemberRepository.findAll()).hasSize(0)
        );
    }

    @DisplayName("본인 것이 아닌 여행 상세를 삭제하려고 하면 예외가 발생한다.")
    @Test
    void cannotDeleteTravelIfNotOwner() {
        // given
        Member member = saveMember();
        Member otherMember = saveMember();
        MemoryIdResponse memoryIdResponse = memoryService.createMemory(createTravelRequest(2023), null, member);

        // when & then
        assertThatThrownBy(() -> memoryService.deleteMemory(memoryIdResponse.memoryId(), otherMember))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("요청하신 작업을 처리할 권한이 없습니다.");
    }

    @DisplayName("방문기록이 존재하는 여행 상세에 삭제를 시도할 경우 예외가 발생한다.")
    @Test
    void failDeleteTravelByExistingVisits() {
        // given
        Member member = saveMember();
        MemoryIdResponse memoryIdResponse = memoryService.createMemory(createTravelRequest(2023), null, member);
        saveVisit(LocalDateTime.of(2024, 7, 10, 10, 0), memoryIdResponse.memoryId());

        // when & then
        assertThatThrownBy(() -> memoryService.deleteMemory(memoryIdResponse.memoryId(), member))
                .isInstanceOf(StaccatoException.class)
                .hasMessage("해당 여행 상세에 방문 기록이 남아있어 삭제할 수 없습니다.");
    }

    private Member saveMember() {
        return memberRepository.save(Member.builder().nickname("staccato").build());
    }
}
