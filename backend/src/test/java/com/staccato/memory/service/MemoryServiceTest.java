package com.staccato.memory.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import com.staccato.ServiceSliceTest;
import com.staccato.comment.domain.Comment;
import com.staccato.comment.repository.CommentRepository;
import com.staccato.exception.ForbiddenException;
import com.staccato.exception.StaccatoException;
import com.staccato.fixture.Member.MemberFixture;
import com.staccato.fixture.comment.CommentFixture;
import com.staccato.fixture.memory.MemoryRequestFixture;
import com.staccato.fixture.moment.MomentFixture;
import com.staccato.member.domain.Member;
import com.staccato.member.repository.MemberRepository;
import com.staccato.memory.domain.Memory;
import com.staccato.memory.domain.MemoryMember;
import com.staccato.memory.repository.MemoryMemberRepository;
import com.staccato.memory.repository.MemoryRepository;
import com.staccato.memory.service.dto.request.MemoryRequest;
import com.staccato.memory.service.dto.response.MemoryDetailResponse;
import com.staccato.memory.service.dto.response.MemoryIdResponse;
import com.staccato.memory.service.dto.response.MemoryNameResponses;
import com.staccato.memory.service.dto.response.MemoryResponses;
import com.staccato.memory.service.dto.response.MomentResponse;
import com.staccato.moment.domain.Moment;
import com.staccato.moment.repository.MomentRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

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
    @Autowired
    private CommentRepository commentRepository;

    static Stream<Arguments> dateProvider() {
        return Stream.of(
                Arguments.of(LocalDate.of(2024, 7, 1), 3),
                Arguments.of(LocalDate.of(2024, 7, 2), 2)
        );
    }

    static Stream<Arguments> updateMemoryProvider() {
        return Stream.of(
                Arguments.of(
                        MemoryRequestFixture.create(null, LocalDate.of(2024, 8, 1), LocalDate.of(2024, 8, 10)), null),
                Arguments.of(
                        MemoryRequestFixture.create("imageUrl", LocalDate.of(2024, 8, 1), LocalDate.of(2024, 8, 10)), "imageUrl"));
    }

    @DisplayName("추억 정보를 기반으로, 추억을 생성하고 작성자를 저장한다.")
    @Test
    void createMemory() {
        // given
        MemoryRequest memoryRequest = MemoryRequestFixture.create(LocalDate.of(2024, 7, 1), LocalDate.of(2024, 7, 10));
        Member member = memberRepository.save(MemberFixture.create());

        // when
        MemoryIdResponse memoryIdResponse = memoryService.createMemory(memoryRequest, member);
        MemoryMember memoryMember = memoryMemberRepository.findAllByMemberId(member.getId()).get(0);

        // then
        assertAll(
                () -> assertThat(memoryMember.getMember().getId()).isEqualTo(member.getId()),
                () -> assertThat(memoryMember.getMemory().getId()).isEqualTo(memoryIdResponse.memoryId())
        );
    }

    @DisplayName("추억의 기간이 null이더라도 추억을 생성할 수 있다.")
    @Test
    void createMemoryWithoutTerm() {
        // given
        MemoryRequest memoryRequest = MemoryRequestFixture.create(null, null);
        Member member = memberRepository.save(MemberFixture.create());

        // when
        MemoryIdResponse memoryIdResponse = memoryService.createMemory(memoryRequest, member);
        MemoryMember memoryMember = memoryMemberRepository.findAllByMemberId(member.getId())
                .get(0);

        // then
        assertAll(
                () -> assertThat(memoryMember.getMember().getId()).isEqualTo(member.getId()),
                () -> assertThat(memoryMember.getMemory().getId()).isEqualTo(memoryIdResponse.memoryId())
        );
    }

    @DisplayName("사용자의 추억 중 이미 존재하는 추억 이름으로 추억을 생성할 수 없다.")
    @Test
    void cannotCreateMemoryByDuplicatedTitle() {
        // given
        MemoryRequest memoryRequest = MemoryRequestFixture.create(LocalDate.of(2024, 7, 1), LocalDate.of(2024, 7, 10));
        Member member = memberRepository.save(MemberFixture.create());
        memoryService.createMemory(memoryRequest, member);

        // when & then
        assertThatThrownBy(() -> memoryService.createMemory(memoryRequest, member))
                .isInstanceOf(StaccatoException.class)
                .hasMessage("같은 이름을 가진 추억이 있어요. 다른 이름으로 설정해주세요.");
    }

    @DisplayName("다른 사용자의 이미 존재하는 추억 이름으로 추억을 생성할 수 있다.")
    @Test
    void canCreateMemoryByDuplicatedTitleOfOther() {
        // given
        MemoryRequest memoryRequest = MemoryRequestFixture.create(LocalDate.of(2024, 7, 1), LocalDate.of(2024, 7, 10));
        Member member = memberRepository.save(MemberFixture.create());
        Member otherMember = memberRepository.save(MemberFixture.create("other"));
        memoryService.createMemory(memoryRequest, otherMember);

        // when & then
        assertThatNoException().isThrownBy(() -> memoryService.createMemory(memoryRequest, member));
    }

    @DisplayName("사용자의 모든 추억을 조회하면 생성 시간 기준 내림차순으로 조회된다.")
    @Test
    void readAllMemories() {
        // given
        Member member = memberRepository.save(MemberFixture.create());

        memoryService.createMemory(MemoryRequestFixture.create(LocalDate.of(2023, 7, 1), LocalDate.of(2024, 7, 10), "first"), member);
        memoryService.createMemory(MemoryRequestFixture.create(LocalDate.of(2023, 7, 1), LocalDate.of(2024, 7, 10), "second"), member);

        // when
        MemoryResponses memoryResponses = memoryService.readAllMemories(member);

        // then
        assertAll(
                () -> assertThat(memoryResponses.memories().get(0).memoryTitle()).isEqualTo("second"),
                () -> assertThat(memoryResponses.memories().get(1).memoryTitle()).isEqualTo("first")
        );
    }

    @DisplayName("현재 날짜를 포함하는 모든 추억 목록을 조회한다.")
    @MethodSource("dateProvider")
    @ParameterizedTest
    void readAllMemories(LocalDate currentDate, int expectedSize) {
        // given
        Member member = memberRepository.save(MemberFixture.create());
        memoryService.createMemory(MemoryRequestFixture.create(LocalDate.of(2024, 7, 1), LocalDate.of(2024, 7, 1), "title1"), member);
        memoryService.createMemory(MemoryRequestFixture.create(LocalDate.of(2024, 7, 1), LocalDate.of(2024, 7, 2), "title2"), member);
        memoryService.createMemory(MemoryRequestFixture.create(null, null, "title3"), member);

        // when
        MemoryNameResponses memoryNameResponses = memoryService.readAllMemoriesIncludingDate(member, currentDate);

        // then
        assertThat(memoryNameResponses.memories()).hasSize(expectedSize);
    }

    @DisplayName("현재 날짜를 포함하는 모든 추억 목록을 생성 시간 기준 내림차순으로 조회한다.")
    @Test
    void readAllMemoriesOrderByCreatedAtDesc() {
        // given
        LocalDate currentDate = LocalDate.of(2024, 7, 1);
        Member member = memberRepository.save(MemberFixture.create());
        memoryService.createMemory(MemoryRequestFixture.create(LocalDate.of(2024, 7, 1), LocalDate.of(2024, 7, 1), "title1"), member);
        memoryService.createMemory(MemoryRequestFixture.create(LocalDate.of(2024, 7, 1), LocalDate.of(2024, 7, 2), "title2"), member);

        // when
        MemoryNameResponses memoryNameResponses = memoryService.readAllMemoriesIncludingDate(member, currentDate);

        // then
        assertAll(
                () -> assertThat(memoryNameResponses.memories().get(0).memoryTitle()).isEqualTo("title2"),
                () -> assertThat(memoryNameResponses.memories().get(1).memoryTitle()).isEqualTo("title1")
        );
    }

    @DisplayName("특정 추억을 조회한다.")
    @Test
    void readMemoryById() {
        // given
        Member member = memberRepository.save(MemberFixture.create());

        MemoryIdResponse memoryIdResponse = memoryService.createMemory(MemoryRequestFixture.create(LocalDate.of(2023, 7, 1), LocalDate.of(2024, 7, 10)), member);

        // when
        MemoryDetailResponse memoryDetailResponse = memoryService.readMemoryById(memoryIdResponse.memoryId(), member);

        // then
        assertAll(
                () -> assertThat(memoryDetailResponse.memoryId()).isEqualTo(memoryIdResponse.memoryId()),
                () -> assertThat(memoryDetailResponse.mates()).hasSize(1)
        );
    }

    @DisplayName("기간이 없는 특정 추억을 조회한다.")
    @Test
    void readMemoryByIdWithoutTerm() {
        // given
        Member member = memberRepository.save(MemberFixture.create());

        MemoryIdResponse memoryIdResponse = memoryService.createMemory(MemoryRequestFixture.create(null, null), member);

        // when
        MemoryDetailResponse memoryDetailResponse = memoryService.readMemoryById(memoryIdResponse.memoryId(), member);

        // then
        assertAll(
                () -> assertThat(memoryDetailResponse.memoryId()).isEqualTo(memoryIdResponse.memoryId()),
                () -> assertThat(memoryDetailResponse.mates()).hasSize(1),
                () -> assertThat(memoryDetailResponse.startAt()).isNull(),
                () -> assertThat(memoryDetailResponse.endAt()).isNull()
        );
    }

    @DisplayName("본인 것이 아닌 특정 추억을 조회하려고 하면 예외가 발생한다.")
    @Test
    void cannotReadMemoryByIdIfNotOwner() {
        // given
        Member member = memberRepository.save(MemberFixture.create("member"));
        Member otherMember = memberRepository.save(MemberFixture.create("otherMember"));

        MemoryIdResponse memoryIdResponse = memoryService.createMemory(MemoryRequestFixture.create(LocalDate.of(2023, 7, 1), LocalDate.of(2024, 7, 10)), member);

        // when & then
        assertThatThrownBy(() -> memoryService.readMemoryById(memoryIdResponse.memoryId(), otherMember))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("요청하신 작업을 처리할 권한이 없습니다.");
    }

    @DisplayName("특정 추억을 조회하면 스타카토는 최신순으로 반환한다.")
    @Test
    void readMemoryByIdOrderByVisitedAt() {
        // given
        Member member = memberRepository.save(MemberFixture.create());

        MemoryIdResponse memoryIdResponse = memoryService.createMemory(MemoryRequestFixture.create(LocalDate.of(2023, 7, 1), LocalDate.of(2024, 7, 10)), member);
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
                        .containsExactly(lastMoment.getId(), secondMoment.getId(), firstMoment.getId())
        );
    }

    @DisplayName("존재하지 않는 추억을 조회하려고 할 경우 예외가 발생한다.")
    @Test
    void failReadMemory() {
        // given
        Member member = memberRepository.save(MemberFixture.create());
        long unknownId = 1;

        // when & then
        assertThatThrownBy(() -> memoryService.readMemoryById(unknownId, member))
                .isInstanceOf(StaccatoException.class)
                .hasMessage("요청하신 추억을 찾을 수 없어요.");
    }

    @DisplayName("추억 정보를 기반으로, 추억을 수정한다.")
    @MethodSource("updateMemoryProvider")
    @ParameterizedTest
    void updateMemory(MemoryRequest updatedMemory, String expected) {
        // given
        Member member = memberRepository.save(MemberFixture.create());
        MemoryIdResponse memoryResponse = memoryService.createMemory(MemoryRequestFixture.create(LocalDate.of(2024, 7, 1), LocalDate.of(2024, 7, 10)), member);

        // when
        memoryService.updateMemory(updatedMemory, memoryResponse.memoryId(), member);
        Memory foundedMemory = memoryRepository.findById(memoryResponse.memoryId()).get();

        // then
        assertAll(
                () -> assertThat(foundedMemory.getId()).isEqualTo(memoryResponse.memoryId()),
                () -> assertThat(foundedMemory.getTitle()).isEqualTo(updatedMemory.memoryTitle()),
                () -> assertThat(foundedMemory.getDescription()).isEqualTo(updatedMemory.description()),
                () -> assertThat(foundedMemory.getTerm().getStartAt()).isEqualTo(updatedMemory.startAt()),
                () -> assertThat(foundedMemory.getTerm().getEndAt()).isEqualTo(updatedMemory.endAt()),
                () -> assertThat(foundedMemory.getThumbnailUrl()).isEqualTo(expected)
        );
    }

    @DisplayName("기간이 존재하는 추억에 대해 기간이 존재하지 않도록 변경할 수 있다.")
    @Test
    void updateMemoryWithNullableTerm() {
        // given
        Member member = memberRepository.save(MemberFixture.create());
        MemoryIdResponse memoryResponse = memoryService.createMemory(MemoryRequestFixture.create(LocalDate.of(2024, 7, 1), LocalDate.of(2024, 7, 10)), member);

        // when
        MemoryRequest memoryUpdateRequest = MemoryRequestFixture.create(null, null);
        memoryService.updateMemory(memoryUpdateRequest, memoryResponse.memoryId(), member);
        Memory foundedMemory = memoryRepository.findById(memoryResponse.memoryId()).get();

        // then
        assertAll(
                () -> assertThat(foundedMemory.getTerm().getStartAt()).isNull(),
                () -> assertThat(foundedMemory.getTerm().getEndAt()).isNull()
        );
    }

    @DisplayName("존재하지 않는 추억을 수정하려 할 경우 예외가 발생한다.")
    @Test
    void failUpdateMemory() {
        // given
        Member member = memberRepository.save(MemberFixture.create());
        MemoryRequest memoryRequest = MemoryRequestFixture.create(LocalDate.of(2023, 7, 1), LocalDate.of(2024, 7, 10));

        // when & then
        assertThatThrownBy(() -> memoryService.updateMemory(memoryRequest, 1L, member))
                .isInstanceOf(StaccatoException.class)
                .hasMessage("요청하신 추억을 찾을 수 없어요.");
    }

    @DisplayName("본인 것이 아닌 추억을 수정하려고 하면 예외가 발생한다.")
    @Test
    void cannotUpdateMemoryIfNotOwner() {
        // given
        Member member = memberRepository.save(MemberFixture.create("member"));
        Member otherMember = memberRepository.save(MemberFixture.create("otherMember"));
        MemoryRequest updatedMemory = MemoryRequestFixture.create(LocalDate.of(2023, 7, 1), LocalDate.of(2024, 7, 10));
        MemoryIdResponse memoryIdResponse = memoryService.createMemory(MemoryRequestFixture.create(LocalDate.of(2023, 7, 1), LocalDate.of(2024, 7, 10)), member);

        // when & then
        assertThatThrownBy(() -> memoryService.updateMemory(updatedMemory, memoryIdResponse.memoryId(), otherMember))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("요청하신 작업을 처리할 권한이 없습니다.");
    }

    @DisplayName("본래 해당 추억의 이름과 동일한 이름으로 추억을 수정할 수 있다.")
    @Test
    void updateMemoryByOriginTitle() {
        // given
        MemoryRequest memoryRequest = MemoryRequestFixture.create(LocalDate.of(2024, 7, 1), LocalDate.of(2024, 7, 10));
        Member member = memberRepository.save(MemberFixture.create());
        MemoryIdResponse memoryIdResponse = memoryService.createMemory(memoryRequest, member);

        // when & then
        assertThatNoException().isThrownBy(() -> memoryService.updateMemory(memoryRequest, memoryIdResponse.memoryId(), member));
    }

    @DisplayName("이미 존재하는 이름으로 추억을 수정할 수 없다.")
    @Test
    void cannotUpdateMemoryByDuplicatedTitle() {
        // given
        Member member = memberRepository.save(MemberFixture.create());
        MemoryRequest memoryRequest1 = MemoryRequestFixture.create(LocalDate.of(2024, 7, 1), LocalDate.of(2024, 7, 10), "existingTitle");
        memoryService.createMemory(memoryRequest1, member);
        MemoryRequest memoryRequest2 = MemoryRequestFixture.create(LocalDate.of(2024, 7, 1), LocalDate.of(2024, 7, 10), "otherTitle");
        MemoryIdResponse memoryIdResponse = memoryService.createMemory(memoryRequest2, member);

        // when & then
        assertThatThrownBy(() -> memoryService.updateMemory(memoryRequest1, memoryIdResponse.memoryId(), member))
                .isInstanceOf(StaccatoException.class)
                .hasMessage("같은 이름을 가진 추억이 있어요. 다른 이름으로 설정해주세요.");
        ;
    }

    @DisplayName("추억 식별값을 통해 추억을 삭제한다.")
    @Test
    void deleteMemory() {
        // given
        Member member = memberRepository.save(MemberFixture.create());
        MemoryIdResponse memoryIdResponse = memoryService.createMemory(MemoryRequestFixture.create(LocalDate.of(2023, 7, 1), LocalDate.of(2024, 7, 10)), member);

        // when
        memoryService.deleteMemory(memoryIdResponse.memoryId(), member);

        // then
        assertAll(
                () -> assertThat(memoryRepository.findById(memoryIdResponse.memoryId())).isEmpty(),
                () -> assertThat(memoryMemberRepository.findAll()).hasSize(0)
        );
    }

    @DisplayName("추억을 삭제하면 속한 스타카토들도 함께 삭제된다.")
    @Test
    void deleteMemoryWithMoment() {
        // given
        Member member = memberRepository.save(MemberFixture.create());
        MemoryIdResponse memoryIdResponse = memoryService.createMemory(MemoryRequestFixture.create(LocalDate.of(2023, 7, 1), LocalDate.of(2024, 7, 10)), member);
        Moment moment = saveMoment(LocalDateTime.of(2023, 7, 2, 10, 10), memoryIdResponse.memoryId());
        Comment comment = commentRepository.save(CommentFixture.create(moment, member));

        // when
        memoryService.deleteMemory(memoryIdResponse.memoryId(), member);

        // then
        assertAll(
                () -> assertThat(memoryRepository.findById(memoryIdResponse.memoryId())).isEmpty(),
                () -> assertThat(memoryMemberRepository.findAll()).isEmpty(),
                () -> assertThat(momentRepository.findAll()).isEmpty(),
                () -> assertThat(commentRepository.findAll()).isEmpty()
        );
    }

    private Moment saveMoment(LocalDateTime visitedAt, long memoryId) {
        return momentRepository.save(MomentFixture.create(memoryRepository.findById(memoryId).get(), visitedAt));
    }

    @DisplayName("본인 것이 아닌 추억 상세를 삭제하려고 하면 예외가 발생한다.")
    @Test
    void cannotDeleteMemoryIfNotOwner() {
        // given
        Member member = memberRepository.save(MemberFixture.create("member"));
        Member otherMember = memberRepository.save(MemberFixture.create("otherMember"));
        MemoryIdResponse memoryIdResponse = memoryService.createMemory(MemoryRequestFixture.create(LocalDate.of(2023, 7, 1), LocalDate.of(2024, 7, 10)), member);

        // when & then
        assertThatThrownBy(() -> memoryService.deleteMemory(memoryIdResponse.memoryId(), otherMember))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("요청하신 작업을 처리할 권한이 없습니다.");
    }
}
