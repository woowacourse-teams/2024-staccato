package com.staccato.comment.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import com.staccato.exception.StaccatoException;
import com.staccato.fixture.category.CategoryFixtures;
import com.staccato.fixture.member.MemberFixtures;
import com.staccato.fixture.staccato.StaccatoFixtures;
import com.staccato.member.domain.Member;
import com.staccato.staccato.domain.Staccato;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CommentTest {
    private Member member;
    private Staccato staccato;

    @BeforeEach
    void init() {
        member = MemberFixtures.defaultMember().build();
        staccato = StaccatoFixtures.defaultStaccato()
                .withCategory(CategoryFixtures.defaultCategory().build())
                .build();
    }

    @DisplayName("정상적인 댓글은 생성에 성공한다.")
    @Test
    void createValidComment() {
        // given
        String content = "content";

        // when & then
        assertThatNoException().isThrownBy(() -> new Comment(content, staccato, member));
    }

    @DisplayName("댓글 내용이 1자 미만, 500자 초과이면 예외가 발생한다.")
    @ParameterizedTest
    @ValueSource(ints = {0, 501})
    void cannotCreateIfContentLengthTooLong(int count) {
        // given
        String content = "가".repeat(count);

        // when & then
        assertThatThrownBy(() -> new Comment(content, staccato, member))
                .isInstanceOf(StaccatoException.class)
                .hasMessageContaining("댓글은 공백 포함 500자 이하로 입력해주세요.");
    }

    @DisplayName("댓글 내용이 1자 미만, 500자 초과이면 수정할 수 없다.")
    @ParameterizedTest
    @ValueSource(ints = {0, 501})
    void cannotChangeContentIfTooLong(int count) {
        // given
        Comment comment = new Comment("original", staccato, member);

        // when
        assertThatThrownBy(() -> comment.changeContent("가".repeat(count)))
                .isInstanceOf(StaccatoException.class)
                .hasMessage("댓글은 공백 포함 500자 이하로 입력해주세요.");
    }
}
