package com.staccato.category.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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

    @Nested
    @DisplayName("카테고리의 소유자를 검증한다.")
    class ValidateCategoryOwner {

        private Member host;
        private Member guest;
        private Category category;

        @BeforeEach
        void setUp() {
            host = MemberFixtures.defaultMember().withNickname("host").build();
            guest = MemberFixtures.defaultMember().withNickname("guest").build();
            category = CategoryFixtures.defaultCategory()
                    .withHost(host)
                    .withGuests(List.of(guest))
                    .build();
        }

        @DisplayName("카테고리의 HOST는 카테고리의 소유자이다.")
        @Test
        void successValidateOwnerIfMemberIsCategoryHost() {
            assertDoesNotThrow(() -> category.validateOwner(host));
        }

        @DisplayName("카테고리의 GUEST는 카테고리의 소유자이다.")
        @Test
        void successValidateOwnerIfMemberIsCategoryGuest() {
            assertDoesNotThrow(() -> category.validateOwner(guest));
        }

        @DisplayName("카테고리의 함께하는 사람이 아니면 카테고리 안에 있는 스타카토의 소유자가 아니다.")
        @Test
        void failValidateOwnerIfMemberNotInCategory() {
            Member other = MemberFixtures.defaultMember().withNickname("other").build();
            assertThatThrownBy(() -> category.validateOwner(other))
                    .isInstanceOf(ForbiddenException.class);
        }
    }

    @Nested
    @DisplayName("HOST 및 GUEST를 검증한다.")
    class ValidateHostOrGuest {

        private Member host;
        private Member guest;
        private Category category;

        @BeforeEach
        void setUp() {
            host = MemberFixtures.defaultMember().withNickname("host").build();
            guest = MemberFixtures.defaultMember().withNickname("guest").build();
            category = CategoryFixtures.defaultCategory()
                    .withHost(host)
                    .withGuests(List.of(guest))
                    .build();
        }

        @DisplayName("validateHost는 멤버가 HOST라면 예외를 발생시키지 않는다.")
        @Test
        void successValidateHostIfMemberIsHost() {
            assertDoesNotThrow(() -> category.validateHost(host));
        }

        @DisplayName("validateHost는 멤버가 GUEST라면 예외를 발생시킨다.")
        @Test
        void failValidateHostIfMemberIsGuest() {
            assertThatThrownBy(() -> category.validateHost(guest))
                    .isInstanceOf(ForbiddenException.class);
        }

        @DisplayName("validateHost는 멤버가 카테고리에 소속되어 있지 않으면 예외를 발생시킨다.")
        @Test
        void failValidateHostIfMemberNotInCategory() {
            Member other = MemberFixtures.defaultMember().withNickname("other").build();
            assertThatThrownBy(() -> category.validateHost(other))
                    .isInstanceOf(ForbiddenException.class);
        }

        @DisplayName("validateGuest는 멤버가 GUEST라면 예외를 발생시키지 않는다.")
        @Test
        void successValidateGuestIfMemberIsGuest() {
            assertDoesNotThrow(() -> category.validateNotHost(guest));
        }

        @DisplayName("validateGuest는 멤버가 HOST라면 예외를 발생시킨다.")
        @Test
        void failValidateGuestIfMemberIsHost() {
            assertThatThrownBy(() -> category.validateNotHost(host))
                    .isInstanceOf(ForbiddenException.class);
        }

        @DisplayName("validateGuest는 멤버가 카테고리에 소속되어 있지 않으면 예외를 발생시킨다.")
        @Test
        void failValidateGuestIfMemberNotInCategory() {
            Member other = MemberFixtures.defaultMember().withNickname("other").build();
            assertThatThrownBy(() -> category.validateNotHost(other))
                    .isInstanceOf(ForbiddenException.class);
        }
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
