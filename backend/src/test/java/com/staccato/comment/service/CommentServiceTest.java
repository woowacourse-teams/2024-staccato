package com.staccato.comment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.staccato.ServiceSliceTest;
import com.staccato.category.domain.Category;
import com.staccato.category.repository.CategoryRepository;
import com.staccato.comment.domain.Comment;
import com.staccato.comment.repository.CommentRepository;
import com.staccato.comment.service.dto.request.CommentRequest;
import com.staccato.comment.service.dto.request.CommentUpdateRequest;
import com.staccato.comment.service.dto.response.CommentResponse;
import com.staccato.comment.service.dto.response.CommentResponses;
import com.staccato.exception.ForbiddenException;
import com.staccato.exception.StaccatoException;
import com.staccato.fixture.category.CategoryFixtures;
import com.staccato.fixture.comment.CommentFixtures;
import com.staccato.fixture.comment.CommentRequestFixtures;
import com.staccato.fixture.comment.CommentUpdateRequestFixtures;
import com.staccato.fixture.member.MemberFixtures;
import com.staccato.fixture.staccato.StaccatoFixtures;
import com.staccato.member.domain.Member;
import com.staccato.member.repository.MemberRepository;
import com.staccato.staccato.domain.Staccato;
import com.staccato.staccato.repository.StaccatoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class CommentServiceTest extends ServiceSliceTest {

    @Autowired
    private CommentService commentService;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private StaccatoRepository staccatoRepository;

    @DisplayName("스타카토가 존재하면 댓글 생성에 성공한다.")
    @Test
    void createComment() {
        // given
        Member member = MemberFixtures.defaultMember().buildAndSave(memberRepository);
        Category category = CategoryFixtures.defaultCategory()
                .buildAndSaveWithMember(member, categoryRepository);
        StaccatoFixtures.defaultStaccato()
                .withCategory(category).buildAndSave(staccatoRepository);
        CommentRequest commentRequest = CommentRequestFixtures.defaultCommentRequest().build();

        // when
        long commentId = commentService.createComment(commentRequest, member);

        // then
        assertThat(commentRepository.findById(commentId)).isNotEmpty();
    }

    @DisplayName("존재하지 않는 스타카토에 댓글 생성을 시도하면 예외가 발생한다.")
    @Test
    void createCommentFailByNotExistStaccato() {
        // given
        Member member = MemberFixtures.defaultMember().buildAndSave(memberRepository);
        CommentRequest commentRequest = CommentRequestFixtures.defaultCommentRequest().build();

        // when & then
        assertThatThrownBy(() -> commentService.createComment(commentRequest, member))
            .isInstanceOf(StaccatoException.class)
            .hasMessageContaining("요청하신 스타카토를 찾을 수 없어요.");
    }

    @DisplayName("권한이 없는 스타카토에 댓글 생성을 시도하면 예외가 발생한다.")
    @Test
    void createCommentFailByForbidden() {
        // given
        Member staccatoOwner = MemberFixtures.defaultMember().buildAndSave(memberRepository);
        Member unexpectedMember = MemberFixtures.defaultMember().buildAndSave(memberRepository);
        Category category = CategoryFixtures.defaultCategory()
                .buildAndSaveWithMember(staccatoOwner, categoryRepository);
        StaccatoFixtures.defaultStaccato()
                .withCategory(category).buildAndSave(staccatoRepository);
        CommentRequest commentRequest = CommentRequestFixtures.defaultCommentRequest().build();

        // when & then
        assertThatThrownBy(() -> commentService.createComment(commentRequest, unexpectedMember))
            .isInstanceOf(ForbiddenException.class)
            .hasMessageContaining("요청하신 작업을 처리할 권한이 없습니다.");
    }

    @DisplayName("특정 스타카토에 속한 모든 댓글을 생성 순으로 조회한다.")
    @Test
    void readAllByStaccatoId() {
        // given
        Member member = MemberFixtures.defaultMember().buildAndSave(memberRepository);
        Category category = CategoryFixtures.defaultCategory()
                .buildAndSaveWithMember(member, categoryRepository);
        Staccato staccato = StaccatoFixtures.defaultStaccato()
                .withCategory(category).buildAndSave(staccatoRepository);
        Staccato anotherStaccato = StaccatoFixtures.defaultStaccato()
                .withCategory(category).buildAndSave(staccatoRepository);
        CommentRequest commentRequest1 = CommentRequestFixtures.defaultCommentRequest()
                .withStaccatoId(staccato.getId()).build();
        CommentRequest commentRequest2 = CommentRequestFixtures.defaultCommentRequest()
                .withStaccatoId(staccato.getId()).build();
        CommentRequest commentRequestOfAnotherStaccato = CommentRequestFixtures.defaultCommentRequest()
                .withStaccatoId(anotherStaccato.getId()).build();
        long commentId1 = commentService.createComment(commentRequest1, member);
        long commentId2 = commentService.createComment(commentRequest2, member);
        commentService.createComment(commentRequestOfAnotherStaccato, member);

        // when
        CommentResponses commentResponses = commentService.readAllCommentsByStaccatoId(member,
            staccato.getId());

        // then
        assertThat(commentResponses.comments().stream().map(CommentResponse::commentId).toList())
            .containsExactly(commentId1, commentId2);
    }

    @DisplayName("조회 권한이 없는 스타카토에 달린 댓글들 조회를 시도하면 예외가 발생한다.")
    @Test
    void readAllByStaccatoIdFailByForbidden() {
        // given
        Member staccatoOwner = MemberFixtures.defaultMember().buildAndSave(memberRepository);
        Member unexpectedMember = MemberFixtures.defaultMember().buildAndSave(memberRepository);
        Category category = CategoryFixtures.defaultCategory()
                .buildAndSaveWithMember(staccatoOwner, categoryRepository);
        Staccato staccato = StaccatoFixtures.defaultStaccato()
                .withCategory(category).buildAndSave(staccatoRepository);
        CommentFixtures.defaultComment()
                .withStaccato(staccato)
                .withMember(staccatoOwner).buildAndSave(commentRepository);

        // when & then
        assertThatThrownBy(
            () -> commentService.readAllCommentsByStaccatoId(unexpectedMember, staccato.getId()))
            .isInstanceOf(ForbiddenException.class)
            .hasMessageContaining("요청하신 작업을 처리할 권한이 없습니다.");
    }

    @DisplayName("본인이 쓴 댓글은 수정할 수 있다.")
    @Test
    void updateComment() {
        // given
        Member member = MemberFixtures.defaultMember().buildAndSave(memberRepository);
        Category category = CategoryFixtures.defaultCategory()
                .buildAndSaveWithMember(member, categoryRepository);
        Staccato staccato = StaccatoFixtures.defaultStaccato()
                .withCategory(category).buildAndSave(staccatoRepository);
        Comment comment = CommentFixtures.defaultComment()
                .withStaccato(staccato)
                .withMember(member).buildAndSave(commentRepository);
        CommentUpdateRequest commentUpdateRequest = CommentUpdateRequestFixtures.defaultCommentUpdateRequest().build();

        // when
        commentService.updateComment(member, comment.getId(), commentUpdateRequest);

        // then
        assertThat(commentRepository.findById(comment.getId()).get()
            .getContent()).isEqualTo(commentUpdateRequest.content());
    }

    @DisplayName("수정하려는 댓글을 찾을 수 없는 경우 예외가 발생한다.")
    @Test
    void updateCommentFailByNotExist() {
        // given
        long notExistCommentId = 1;
        CommentUpdateRequest commentUpdateRequest = CommentUpdateRequestFixtures.defaultCommentUpdateRequest().build();

        // when & then
        assertThatThrownBy(
            () -> commentService.updateComment(MemberFixtures.defaultMember().build(), notExistCommentId,
                commentUpdateRequest))
            .isInstanceOf(StaccatoException.class)
            .hasMessageContaining("요청하신 댓글을 찾을 수 없어요.");
    }

    @DisplayName("본인이 달지 않은 댓글에 대해 수정을 시도하면 예외가 발생한다.")
    @Test
    void updateCommentFailByForbidden() {
        // given
        Member staccatoOwner = MemberFixtures.defaultMember().buildAndSave(memberRepository);
        Member unexpectedMember = MemberFixtures.defaultMember().buildAndSave(memberRepository);
        Category category = CategoryFixtures.defaultCategory()
                .buildAndSaveWithMember(staccatoOwner, categoryRepository);
        Staccato staccato = StaccatoFixtures.defaultStaccato()
                .withCategory(category).buildAndSave(staccatoRepository);
        Comment comment = CommentFixtures.defaultComment()
                .withStaccato(staccato)
                .withMember(staccatoOwner)
                .buildAndSave(commentRepository);
        CommentUpdateRequest commentUpdateRequest = CommentUpdateRequestFixtures.defaultCommentUpdateRequest().build();

        // when & then
        assertThatThrownBy(() -> commentService.updateComment(unexpectedMember, comment.getId(),
            commentUpdateRequest))
            .isInstanceOf(ForbiddenException.class)
            .hasMessageContaining("요청하신 작업을 처리할 권한이 없습니다.");
    }

    @DisplayName("본인이 쓴 댓글은 삭제할 수 있다.")
    @Test
    void deleteComment() {
        // given
        Member member = MemberFixtures.defaultMember().buildAndSave(memberRepository);
        Category category = CategoryFixtures.defaultCategory()
                .buildAndSaveWithMember(member, categoryRepository);
        Staccato staccato = StaccatoFixtures.defaultStaccato()
                .withCategory(category).buildAndSave(staccatoRepository);
        Comment comment = CommentFixtures.defaultComment()
                .withStaccato(staccato)
                .withMember(member).buildAndSave(commentRepository);

        // when
        commentService.deleteComment(comment.getId(), member);

        // then
        assertThat(commentRepository.findById(comment.getId())).isEmpty();
    }

    @DisplayName("본인이 쓴 댓글이 아니면 삭제할 수 없다.")
    @Test
    void deleteCommentFail() {
        // given
        Member commentOwner = MemberFixtures.defaultMember().buildAndSave(memberRepository);
        Member unexpectedMember = MemberFixtures.defaultMember().buildAndSave(memberRepository);
        Category category = CategoryFixtures.defaultCategory()
                .buildAndSaveWithMember(commentOwner, categoryRepository);
        Staccato staccato = StaccatoFixtures.defaultStaccato()
                .withCategory(category).buildAndSave(staccatoRepository);
        Comment comment = CommentFixtures.defaultComment()
                .withStaccato(staccato)
                .withMember(commentOwner).buildAndSave(commentRepository);

        // when & then
        assertThatThrownBy(() -> commentService.deleteComment(comment.getId(), unexpectedMember))
            .isInstanceOf(ForbiddenException.class)
            .hasMessageContaining("요청하신 작업을 처리할 권한이 없습니다.");
    }
}
