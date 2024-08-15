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

    static Stream<Arguments> updateMemoryProvider() {
        return Stream.of(
                Arguments.of(
                        new MemoryRequest("imageUrl", "2024 여름 휴가다!", "친한 친구들과 함께한 여름 휴가 추억", LocalDate.of(2024, 8, 1), LocalDate.of(2024, 8, 10)),
                        new MockMultipartFile("memoryThumbnailUrl", "example.jpg".getBytes()), "fakeUrl"),
                Arguments.of(
                        new MemoryRequest(null, "2024 여름 휴가다!", "친한 친구들과 함께한 여름 휴가 추억", LocalDate.of(2024, 8, 1), LocalDate.of(2024, 8, 10)),
                        null, null),
                Arguments.of(
                        new MemoryRequest("imageUrl", "2024 여름 휴가다!", "친한 친구들과 함께한 여름 휴가 추억", LocalDate.of(2024, 8, 1), LocalDate.of(2024, 8, 10)),
                        null, "imageUrl"));
    }

    @DisplayName("추억 상세 정보를 기반으로, 추억 상세를 생성하고 작성자를 저장한다.")
    @Test
    void createMemory() {
        // given
        MemoryRequest memoryRequest = createMemoryRequest(2024);
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

    @DisplayName("조건에 따라 추억 상세 목록을 조회한다.")
    @MethodSource("yearProvider")
    @ParameterizedTest
    void readAllMemorys(Integer year, int expectedSize) {
        // given
        Member member = saveMember();
        memoryService.createMemory(createMemoryRequest(2023), null, member);
        memoryService.createMemory(createMemoryRequest(2024), null, member);

        // when
        MemoryResponses memoryResponses = memoryService.readAllMemories(member, year);

        // then
        assertThat(memoryResponses.memories()).hasSize(expectedSize);
    }

    @DisplayName("특정 추억 상세를 조회한다.")
    @Test
    void readMemoryById() {
        // given
        Member member = saveMember();

        MemoryIdResponse memoryIdResponse = memoryService.createMemory(createMemoryRequest(2023), null, member);

        // when
        MemoryDetailResponse memoryDetailResponse = memoryService.readMemoryById(memoryIdResponse.memoryId(), member);

        // then
        assertAll(
                () -> assertThat(memoryDetailResponse.memoryId()).isEqualTo(memoryIdResponse.memoryId()),
                () -> assertThat(memoryDetailResponse.mates()).hasSize(1)
        );
    }

    @DisplayName("본인 것이 아닌 특정 추억 상세를 조회하려고 하면 예외가 발생한다.")
    @Test
    void cannotReadMemoryByIdIfNotOwner() {
        // given
        Member member = saveMember();
        Member otherMember = saveMember();

        MemoryIdResponse memoryIdResponse = memoryService.createMemory(createMemoryRequest(2023), null, member);

        // when & then
        assertThatThrownBy(() -> memoryService.readMemoryById(memoryIdResponse.memoryId(), otherMember))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("요청하신 작업을 처리할 권한이 없습니다.");
    }

    @DisplayName("특정 추억 상세를 조회하면 순간 기록은 오래된 순으로 반환한다.")
    @Test
    void readMemoryByIdOrderByVisitedAt() {
        // given
        Member member = saveMember();

        MemoryIdResponse memoryIdResponse = memoryService.createMemory(createMemoryRequest(2023), null, member);
        Moment firstMoment = saveMoment(LocalDateTime.of(2023, 7, 1, 10, 0), memoryIdResponse.memoryId());
        Moment secondMoment = saveMoment(LocalDateTime.of(2023, 7, 1, 10, 10), memoryIdResponse.memoryId());
        Moment lastMoment = saveMoment(LocalDateTime.of(2023, 7, 5, 9, 0), memoryIdResponse.memoryId());

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

    private Moment saveMoment(LocalDateTime visitedAt, long memoryId) {
        return momentRepository.save(MomentFixture.create(memoryRepository.findById(memoryId).get(), visitedAt));
    }

    @DisplayName("존재하지 않는 추억 상세를 조회하려고 할 경우 예외가 발생한다.")
    @Test
    void failReadMemory() {
        // given
        Member member = saveMember();
        long unknownId = 1;

        // when & then
        assertThatThrownBy(() -> memoryService.readMemoryById(unknownId, member))
                .isInstanceOf(StaccatoException.class)
                .hasMessage("요청하신 추억을 찾을 수 없어요.");
    }

    @DisplayName("추억 상세 정보를 기반으로, 추억 상세를 수정한다.")
    @MethodSource("updateMemoryProvider")
    @ParameterizedTest
    void updateMemory(MemoryRequest updatedMemory, MockMultipartFile updatedFile, String expected) {
        // given
        Member member = saveMember();
        MockMultipartFile file = new MockMultipartFile("memoryThumbnailUrl", "example.jpg".getBytes());
        MemoryIdResponse memoryResponse = memoryService.createMemory(createMemoryRequest(2024), file, member);

        // when
        memoryService.updateMemory(updatedMemory, memoryResponse.memoryId(), updatedFile, member);
        Memory foundedMemory = memoryRepository.findById(memoryResponse.memoryId()).get();

        // then
        assertAll(
                () -> assertThat(foundedMemory.getId()).isEqualTo(memoryResponse.memoryId()),
                () -> assertThat(foundedMemory.getTitle()).isEqualTo(updatedMemory.memoryTitle()),
                () -> assertThat(foundedMemory.getDescription()).isEqualTo(updatedMemory.description()),
                () -> assertThat(foundedMemory.getStartAt()).isEqualTo(updatedMemory.startAt()),
                () -> assertThat(foundedMemory.getEndAt()).isEqualTo(updatedMemory.endAt()),
                () -> assertThat(foundedMemory.getThumbnailUrl()).isEqualTo(expected)
        );
    }

    @DisplayName("존재하지 않는 추억 상세를 수정하려 할 경우 예외가 발생한다.")
    @Test
    void failUpdateMemory() {
        // given
        Member member = saveMember();
        MemoryRequest memoryRequest = createMemoryRequest(2023);
        MockMultipartFile file = new MockMultipartFile("memoryThumbnailUrl", "example.jpg".getBytes());

        // when & then
        assertThatThrownBy(() -> memoryService.updateMemory(memoryRequest, 1L, file, member))
                .isInstanceOf(StaccatoException.class)
                .hasMessage("요청하신 추억을 찾을 수 없어요.");
    }

    private MemoryRequest createMemoryRequest(int year) {
        return new MemoryRequest(
                "https://example.com/memorys/geumohrm.jpg",
                year + " 여름 휴가",
                "친구들과 함께한 여름 휴가 추억",
                LocalDate.of(year, 7, 1),
                LocalDate.of(2024, 7, 10)
        );
    }

    @DisplayName("본인 것이 아닌 추억 상세를 수정하려고 하면 예외가 발생한다.")
    @Test
    void cannotUpdateMemoryIfNotOwner() {
        // given
        Member member = saveMember();
        Member otherMember = saveMember();
        MemoryRequest updatedMemory = createMemoryRequest(2023);
        MemoryIdResponse memoryIdResponse = memoryService.createMemory(createMemoryRequest(2023), null, member);
        MockMultipartFile file = new MockMultipartFile("memoryThumbnailUrl", "example.jpg".getBytes());

        // when & then
        assertThatThrownBy(() -> memoryService.updateMemory(updatedMemory, memoryIdResponse.memoryId(), file, otherMember))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("요청하신 작업을 처리할 권한이 없습니다.");
    }

    @DisplayName("추억 식별값을 통해 추억 상세를 삭제한다.")
    @Test
    void deleteMemory() {
        // given
        Member member = saveMember();
        MemoryIdResponse memoryIdResponse = memoryService.createMemory(createMemoryRequest(2023), null, member);

        // when
        memoryService.deleteMemory(memoryIdResponse.memoryId(), member);

        // then
        assertAll(
                () -> assertThat(memoryRepository.findById(memoryIdResponse.memoryId())).isEmpty(),
                () -> assertThat(memoryMemberRepository.findAll()).hasSize(0)
        );
    }

    @DisplayName("추억을 삭제하면 속한 순간들도 함께 삭제된다.")
    @Test
    void deleteMemoryWithMoment() {
        // given
        Member member = saveMember();
        MemoryIdResponse memoryIdResponse = memoryService.createMemory(createMemoryRequest(2023), null, member);
        saveMoment(LocalDateTime.of(2023, 7, 2, 10, 10), memoryIdResponse.memoryId());

        // when
        memoryService.deleteMemory(memoryIdResponse.memoryId(), member);

        // then
        assertAll(
                () -> assertThat(memoryRepository.findById(memoryIdResponse.memoryId())).isEmpty(),
                () -> assertThat(memoryMemberRepository.findAll()).hasSize(0),
                () -> assertThat(momentRepository.findAll()).isEmpty()
        );
    }

    @DisplayName("본인 것이 아닌 추억 상세를 삭제하려고 하면 예외가 발생한다.")
    @Test
    void cannotDeleteMemoryIfNotOwner() {
        // given
        Member member = saveMember();
        Member otherMember = saveMember();
        MemoryIdResponse memoryIdResponse = memoryService.createMemory(createMemoryRequest(2023), null, member);

        // when & then
        assertThatThrownBy(() -> memoryService.deleteMemory(memoryIdResponse.memoryId(), otherMember))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("요청하신 작업을 처리할 권한이 없습니다.");
    }

    private Member saveMember() {
        return memberRepository.save(Member.builder().nickname("staccato").build());
    }
}
