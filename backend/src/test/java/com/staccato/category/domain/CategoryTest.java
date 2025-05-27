package com.staccato.category.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.staccato.exception.ForbiddenException;
import com.staccato.exception.StaccatoException;
import com.staccato.fixture.category.CategoryFixtures;
import com.staccato.fixture.member.MemberFixtures;
import com.staccato.fixture.staccato.StaccatoFixtures;
import com.staccato.member.domain.Member;
import com.staccato.staccato.domain.Staccato;

class CategoryTest {
    @DisplayName("기본 카테고리는 멤버 이름으로 만들어진다.")
    @Test
    void createBasicCategoryWithMemberNickname() {
        // given
        String nickname = "staccato";
        Member member = MemberFixtures.defaultMember()
                .withNickname(nickname)
                .build();

        // when
        Category category = Category.basic(member);

        // then
        assertThat(category.getTitle().getTitle()).isEqualTo(nickname + "의 추억");
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
        boolean result = category.isNotSameTitle(category.getTitle());

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

    @DisplayName("카테고리의 함께하는 사람들(HOST, GUEST)은 카테고리를 읽을 수 있다.")
    @Test
    void canReadCategoryWhenHostOrGuest() {
        Member host = MemberFixtures.defaultMember().withNickname("host").build();
        Member guest = MemberFixtures.defaultMember().withNickname("guest").build();
        Category category = CategoryFixtures.defaultCategory()
                .withHost(host)
                .withGuests(List.of(guest))
                .build();

        assertAll(
                () -> assertDoesNotThrow(() -> category.validateReadPermission(host)),
                () -> assertDoesNotThrow(() -> category.validateReadPermission(guest))
        );
    }

    @DisplayName("카테고리의 함께하는 사람들(HOST, GUEST)이 아니면 카테고리를 읽을 수 없다.")
    @Test
    void cannotReadCategoryWhenNotParticipant() {
        Member host = MemberFixtures.defaultMember().withNickname("host").build();
        Member other = MemberFixtures.defaultMember().withNickname("other").build();
        Category category = CategoryFixtures.defaultCategory()
                .withHost(host)
                .build();

        assertThatThrownBy(() -> category.validateReadPermission(other))
                .isInstanceOf(ForbiddenException.class);
    }

    @DisplayName("카테고리의 HOST는 카테고리를 수정할 수 있다.")
    @Test
    void canModifyCategoryWhenHost() {
        Member host = MemberFixtures.defaultMember().withNickname("host").build();
        Category category = CategoryFixtures.defaultCategory()
                .withHost(host)
                .build();

        assertDoesNotThrow(() -> category.validateModifyPermission(host));
    }

    @DisplayName("카테고리의 GUEST는 카테고리를 수정할 수 없다.")
    @Test
    void cannotModifyCategoryWhenGuest() {
        Member host = MemberFixtures.defaultMember().withNickname("host").build();
        Member guest = MemberFixtures.defaultMember().withNickname("guest").build();
        Category category = CategoryFixtures.defaultCategory()
                .withHost(host)
                .withGuests(List.of(guest))
                .build();

        assertThatThrownBy(() -> category.validateModifyPermission(guest))
                .isInstanceOf(ForbiddenException.class);
    }

    @DisplayName("카테고리의 함께하는 사람들(HOST, GUEST)이 아니면 카테고리를 수정할 수 없다.")
    @Test
    void cannotModifyCategoryWhenNotParticipant() {
        Member host = MemberFixtures.defaultMember().withNickname("host").build();
        Member other = MemberFixtures.defaultMember().withNickname("other").build();
        Category category = CategoryFixtures.defaultCategory()
                .withHost(host)
                .build();

        assertThatThrownBy(() -> category.validateModifyPermission(other))
                .isInstanceOf(ForbiddenException.class);
    }

/*
    @DisplayName("처음 카테고리를 생성했을 때 스타카토는 0개이다.")
    @Test
    void createCategoryAndStaccatoCountIsZero() {
        // given
        Category category = Category.builder()
                .title("category")
                .isShared(false)
                .color(Color.LIGHT_GRAY.getName())
                .build();

        // when
        long result = category.getStaccatoCount();

        // then
        assertThat(result).isZero();
    }

    @DisplayName("카테고리를 수정해도, 스타카토 개수는 영향을 받지 않는다.")
    @Test
    void updateCategoryWithoutStaccatoCount() {
        // given
        Category category = Category.builder()
                .title("category")
                .isShared(false)
                .color(Color.LIGHT_GRAY.getName())
                .build();
        category.increaseStaccatoCount();

        // when

    }*/
}
