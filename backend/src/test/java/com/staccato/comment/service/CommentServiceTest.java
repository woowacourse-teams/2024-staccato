package com.staccato.comment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.staccato.ServiceSliceTest;
import com.staccato.comment.domain.Comment;
import com.staccato.comment.repository.CommentRepository;
import com.staccato.comment.service.dto.request.CommentRequest;
import com.staccato.comment.service.dto.request.CommentUpdateRequest;
import com.staccato.comment.service.dto.response.CommentResponse;
import com.staccato.comment.service.dto.response.CommentResponses;
import com.staccato.exception.ForbiddenException;
import com.staccato.exception.StaccatoException;
import com.staccato.fixture.CommentRequestFixture;
import com.staccato.fixture.Member.MemberFixture;
import com.staccato.fixture.memory.MemoryFixture;
import com.staccato.fixture.comment.CommentFixture;
import com.staccato.fixture.comment.CommentUpdateRequestFixture;
import com.staccato.fixture.moment.MomentFixture;
import com.staccato.member.domain.Member;
import com.staccato.member.repository.MemberRepository;
import com.staccato.memory.domain.Memory;
import com.staccato.memory.repository.MemoryRepository;
import com.staccato.moment.domain.Moment;
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

    @DisplayName("스타카토가 존재하면 댓글 생성에 성공한다.")
    @Test
    void createComment() {
        // given
        Member member = memberRepository.save(MemberFixture.create("nickname"));
        Memory memory = memoryRepository.save(MemoryFixture.create(member));
        Moment moment = momentRepository.save(MomentFixture.create(memory));
        CommentRequest commentRequest = new CommentRequest(moment.getId(), "content");

        // when
        long commentId = commentService.createComment(commentRequest, member);

        // then
        assertThat(commentRepository.findById(commentId)).isNotEmpty();
    }

    @DisplayName("존재하지 않는 스타카토에 댓글 생성을 시도하면 예외가 발생한다.")
    @Test
    void createCommentFailByNotExistMoment() {
        // given
        Member member = memberRepository.save(MemberFixture.create("nickname"));
        CommentRequest commentRequest = CommentRequestFixture.create();

        // when & then
        assertThatThrownBy(() -> commentService.createComment(commentRequest, member))
                .isInstanceOf(StaccatoException.class)
                .hasMessageContaining("요청하신 스타카토를 찾을 수 없어요.");
    }

    @DisplayName("권한이 없는 스타카토에 댓글 생성을 시도하면 예외가 발생한다.")
    @Test
    void createCommentFailByForbidden() {
        // given
        Member momentOwner = memberRepository.save(MemberFixture.create("momentOwner"));
        Member unexpectedMember = memberRepository.save(MemberFixture.create("unexpectedMember"));
        Memory memory = memoryRepository.save(MemoryFixture.create(momentOwner));
        momentRepository.save(MomentFixture.create(memory));
        CommentRequest commentRequest = CommentRequestFixture.create();

        // when & then
        assertThatThrownBy(() -> commentService.createComment(commentRequest, unexpectedMember))
                .isInstanceOf(ForbiddenException.class)
                .hasMessageContaining("요청하신 작업을 처리할 권한이 없습니다.");
    }

    @DisplayName("특정 스타카토에 속한 모든 댓글을 생성 순으로 조회한다.")
    @Test
    void readAllByMomentId() {
        // given
        Member member = memberRepository.save(MemberFixture.create("nickname"));
        Memory memory = memoryRepository.save(MemoryFixture.create(member));
        Moment moment = momentRepository.save(MomentFixture.create(memory));
        Moment anotherMoment = momentRepository.save(MomentFixture.create(memory));
        CommentRequest commentRequest1 = CommentRequestFixture.create(moment.getId());
        CommentRequest commentRequest2 = CommentRequestFixture.create(moment.getId());
        CommentRequest commentRequestOfAnotherMoment = CommentRequestFixture.create(anotherMoment.getId());
        long commentId1 = commentService.createComment(commentRequest1, member);
        long commentId2 = commentService.createComment(commentRequest2, member);
        commentService.createComment(commentRequestOfAnotherMoment, member);

        // when
        CommentResponses commentResponses = commentService.readAllCommentsByMomentId(member, moment.getId());

        // then
        assertThat(commentResponses.comments().stream().map(CommentResponse::commentId).toList())
                .containsExactly(commentId1, commentId2);
    }

    @DisplayName("조회 권한이 없는 스타카토에 달린 댓글들 조회를 시도하면 예외가 발생한다.")
    @Test
    void readAllByMomentIdFailByForbidden() {
        // given
        Member momentOwner = memberRepository.save(MemberFixture.create("momentOwner"));
        Member unexpectedMember = memberRepository.save(MemberFixture.create("unexpectedMember"));
        Memory memory = memoryRepository.save(MemoryFixture.create(momentOwner));
        Moment moment = momentRepository.save(MomentFixture.create(memory));
        commentRepository.save(CommentFixture.create(moment, momentOwner));

        // when & then
        assertThatThrownBy(() -> commentService.readAllCommentsByMomentId(unexpectedMember, moment.getId()))
                .isInstanceOf(ForbiddenException.class)
                .hasMessageContaining("요청하신 작업을 처리할 권한이 없습니다.");
    }

    @DisplayName("본인이 쓴 댓글은 수정할 수 있다.")
    @Test
    void updateComment() {
        // given
        Member member = memberRepository.save(MemberFixture.create("nickname"));
        Memory memory = memoryRepository.save(MemoryFixture.create(member));
        Moment moment = momentRepository.save(MomentFixture.create(memory));
        Comment comment = commentRepository.save(CommentFixture.create(moment, member));
        CommentUpdateRequest commentUpdateRequest = CommentUpdateRequestFixture.create();

        // when
        commentService.updateComment(member, comment.getId(), commentUpdateRequest);

        // then
        assertThat(commentRepository.findById(comment.getId()).get().getContent()).isEqualTo(commentUpdateRequest.content());
    }

    @DisplayName("수정하려는 댓글을 찾을 수 없는 경우 예외가 발생한다.")
    @Test
    void updateCommentFailByNotExist() {
        // given
        long notExistCommentId = 1;
        CommentUpdateRequest commentUpdateRequest = CommentUpdateRequestFixture.create();

        // when & then
        assertThatThrownBy(() -> commentService.updateComment(MemberFixture.create(), notExistCommentId, commentUpdateRequest))
                .isInstanceOf(StaccatoException.class)
                .hasMessageContaining("요청하신 댓글을 찾을 수 없어요.");
    }

    @DisplayName("본인이 달지 않은 댓글에 대해 수정을 시도하면 예외가 발생한다.")
    @Test
    void updateCommentFailByForbidden() {
        // given
        Member momentOwner = memberRepository.save(MemberFixture.create("momentOwner"));
        Member unexpectedMember = memberRepository.save(MemberFixture.create("unexpectedMember"));
        Memory memory = memoryRepository.save(MemoryFixture.create(momentOwner));
        Moment moment = momentRepository.save(MomentFixture.create(memory));
        Comment comment = commentRepository.save(CommentFixture.create(moment, momentOwner));
        CommentUpdateRequest commentUpdateRequest = CommentUpdateRequestFixture.create();

        // when & then
        assertThatThrownBy(() -> commentService.updateComment(unexpectedMember, comment.getId(), commentUpdateRequest))
                .isInstanceOf(ForbiddenException.class)
                .hasMessageContaining("요청하신 작업을 처리할 권한이 없습니다.");
    }

    @DisplayName("본인이 쓴 댓글은 삭제할 수 있다.")
    @Test
    void deleteComment() {
        // given
        Member member = memberRepository.save(MemberFixture.create("nickname"));
        Memory memory = memoryRepository.save(MemoryFixture.create(member));
        Moment moment = momentRepository.save(MomentFixture.create(memory));
        Comment comment = commentRepository.save(CommentFixture.create(moment, member));

        // when
        commentService.deleteComment(comment.getId(), member);

        // then
        assertThat(commentRepository.findById(comment.getId())).isEmpty();
    }

    @DisplayName("본인이 쓴 댓글이 아니면 삭제할 수 없다.")
    @Test
    void deleteCommentFail() {
        // given
        Member commentOwner = memberRepository.save(MemberFixture.create("commentOwner"));
        Member unexpectedMember = memberRepository.save(MemberFixture.create("unexpectedMember"));
        Memory memory = memoryRepository.save(MemoryFixture.create(commentOwner));
        Moment moment = momentRepository.save(MomentFixture.create(memory));
        Comment comment = commentRepository.save(CommentFixture.create(moment, commentOwner));

        // when & then
        assertThatThrownBy(() -> commentService.deleteComment(comment.getId(), unexpectedMember))
                .isInstanceOf(ForbiddenException.class)
                .hasMessageContaining("요청하신 작업을 처리할 권한이 없습니다.");
    }
}
