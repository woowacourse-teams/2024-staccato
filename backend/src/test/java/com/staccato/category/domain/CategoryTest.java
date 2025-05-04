package com.staccato.category.domain;

import com.staccato.fixture.category.CategoryFixtures;
import com.staccato.fixture.staccato.StaccatoFixtures;
import com.staccato.staccato.domain.Staccato;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.staccato.exception.StaccatoException;
import com.staccato.member.domain.Nickname;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CategoryTest {
    @DisplayName("카테고리 생성 시 제목에는 앞뒤 공백이 잘린다.")
    @Test
    void trimCategoryTitle() {
        // given
        String expectedTitle = "title";

        // when
        Category category = Category.builder()
                .title(" title ")
                .color(Color.BLUE.getName())
                .isShared(false)
                .build();

        // then
        assertThat(category.getTitle()).isEqualTo(expectedTitle);
    }

    @DisplayName("기본 카테고리는 멤버 이름으로 만들어진다.")
    @Test
    void createBasicCategoryWithMemberNickname() {
        // given
        String nickname = "staccato";

        // when
        Category category = Category.basic(new Nickname(nickname));

        // then
        assertThat(category.getTitle()).isEqualTo(nickname + "의 추억");
    }

    @DisplayName("카테고리를 수정 시 기존 스타카토 기록 날짜를 포함하지 않는 경우 수정에 실패한다.")
    @Test
    void validateDuration() {
        // given
        Category category = CategoryFixtures.defaultCategory()
                .withTerm(LocalDate.of(2024, 1, 1),
                        LocalDate.of(2024, 12, 31)).build();
        Category updatedCategory = CategoryFixtures.defaultCategory()
                .withTerm(LocalDate.of(2023, 1, 1),
                        LocalDate.of(2023, 12, 31)).build();
        Staccato staccato = StaccatoFixtures.defaultStaccato()
                .withVisitedAt(LocalDateTime.of(2024, 6, 1, 0, 0))
                .withCategory(category).build();

        // when & then
        assertThatThrownBy(() -> category.update(updatedCategory, List.of(staccato)))
                .isInstanceOf(StaccatoException.class)
                .hasMessage("기간이 이미 존재하는 스타카토를 포함하지 않아요. 다시 설정해주세요.");
    }

    @DisplayName("주어진 문자열과 제목이 같으면 거짓을 반환한다.")
    @Test
    void isNotSameTitle() {
        // given
        String title = "title";
        Category category = CategoryFixtures.defaultCategory()
                .withTitle(title).build();

        // when
        boolean result = category.isNotSameTitle(title);

        // then
        assertThat(result).isFalse();
    }

    @DisplayName("카테고리가 기간을 가지고 있으면 참을 반환한다.")
    @Test
    void hasTerm() {
        // given
        Category category = CategoryFixtures.defaultCategory()
                .withTerm(LocalDate.of(2024, 1, 1),
                        LocalDate.of(2024, 12, 31)).build();

        // when
        boolean result = category.hasTerm();

        // then
        assertThat(result).isTrue();
    }

    @DisplayName("카테고리가 기간을 가지고 있지 않으면 거짓을 반환한다.")
    @Test
    void doesNotHaveTerm() {
        // given
        Category category = CategoryFixtures.defaultCategory()
                .withTerm(null, null).build();

        // when
        boolean result = category.hasTerm();

        // then
        assertThat(result).isFalse();
    }

    @DisplayName("카테고리의 색깔을 변경한다.")
    @Test
    void changeColor() {
        // given
        Category category = Category.builder()
                .title("title")
                .color(Color.GREEN.getName())
                .isShared(false)
                .build();

        // when
        category.changeColor(Color.BLUE);

        // then
        assertThat(category.getColor()).isEqualTo(Color.BLUE);
    }
}
