package com.staccato.comment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.staccato.ServiceSliceTest;
import com.staccato.comment.repository.CommentRepository;
import com.staccato.comment.service.dto.request.CommentRequest;
import com.staccato.comment.service.dto.response.CommentResponse;
import com.staccato.comment.service.dto.response.CommentResponses;
import com.staccato.exception.ForbiddenException;
import com.staccato.exception.StaccatoException;
import com.staccato.member.domain.Member;
import com.staccato.member.repository.MemberRepository;
import com.staccato.memory.domain.Memory;
import com.staccato.memory.repository.MemoryRepository;
import com.staccato.moment.domain.Moment;
import com.staccato.moment.fixture.MomentFixture;
import com.staccato.moment.repository.MomentRepository;

class CommentServiceTest extends ServiceSliceTest {
    @Autowired
    private CommentService commentService;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MemoryRepository memoryRepository;
    @Autowired
    private MomentRepository momentRepository;

    @DisplayName("순간이 존재하면 댓글 생성에 성공한다.")
    @Test
    void createComment() {
        // given
        Member member = saveMember("member");
        Memory memory = saveMemory(member);
        Moment moment = saveMoment(memory);
        CommentRequest commentRequest = new CommentRequest(moment.getId(), "content");

        // when
        long commentId = commentService.createComment(commentRequest, member);

        // then
        assertThat(commentRepository.findById(commentId)).isNotEmpty();
    }

    @DisplayName("존재하지 않는 순간에 댓글 생성을 시도하면 예외가 발생한다.")
    @Test
    void createCommentFailByNotExistMoment() {
        // given
        Member member = saveMember("member");
        CommentRequest commentRequest = new CommentRequest(1L, "content");

        // when & then
        assertThatThrownBy(() -> commentService.createComment(commentRequest, member))
                .isInstanceOf(StaccatoException.class)
                .hasMessageContaining("요청하신 순간을 찾을 수 없어요.");
    }

    @DisplayName("권한이 없는 순간에 댓글 생성을 시도하면 예외가 발생한다.")
    @Test
    void createCommentFailByForbidden() {
        // given
        Member momentOwner = saveMember("momentOwner");
        Member unexpectedMember = saveMember("unexpectedMember");
        Memory memory = saveMemory(momentOwner);
        saveMoment(memory);
        CommentRequest commentRequest = new CommentRequest(1L, "content");

        // when & then
        assertThatThrownBy(() -> commentService.createComment(commentRequest, unexpectedMember))
                .isInstanceOf(ForbiddenException.class)
                .hasMessageContaining("요청하신 작업을 처리할 권한이 없습니다.");
    }

    @DisplayName("특정 순간에 속한 모든 댓글을 생성 순으로 조회한다.")
    @Test
    void readAllByMomentId() {
        // given
        Member member = saveMember("member");
        Memory memory = saveMemory(member);
        Moment moment = saveMoment(memory);
        Moment anotherMoment = saveMoment(memory);
        CommentRequest commentRequest1 = new CommentRequest(moment.getId(), "content");
        CommentRequest commentRequest2 = new CommentRequest(moment.getId(), "content");
        CommentRequest commentRequestOfAnotherMoment = new CommentRequest(anotherMoment.getId(), "content");
        long commentId1 = commentService.createComment(commentRequest1, member);
        long commentId2 = commentService.createComment(commentRequest2, member);
        commentService.createComment(commentRequestOfAnotherMoment, member);

        // when
        CommentResponses commentResponses = commentService.readAllCommentsByMomentId(member, moment.getId());

        // then
        assertThat(commentResponses.comments().stream().map(CommentResponse::commentId).toList())
                .containsExactly(commentId1, commentId2);
    }

    private Member saveMember(String nickname) {
        return memberRepository.save(Member.builder().nickname(nickname).build());
    }

    private Memory saveMemory(Member member) {
        Memory memory = Memory.builder().title("Sample Memory").startAt(LocalDate.now()).endAt(LocalDate.now().plusDays(1)).build();
        memory.addMemoryMember(member);

        return memoryRepository.save(memory);
    }

    private Moment saveMoment(Memory memory) {
        Moment moment = MomentFixture.create(memory, LocalDateTime.now());

        return momentRepository.save(moment);
    }
}
