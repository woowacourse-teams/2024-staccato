package com.staccato.staccato.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import com.staccato.category.domain.Category;
import com.staccato.category.domain.Color;
import com.staccato.category.repository.CategoryRepository;
import com.staccato.exception.ForbiddenException;
import com.staccato.exception.StaccatoException;
import com.staccato.fixture.category.CategoryFixtures;
import com.staccato.fixture.member.MemberFixtures;
import com.staccato.fixture.staccato.StaccatoFixtures;
import com.staccato.member.domain.Member;
import com.staccato.member.repository.MemberRepository;
import com.staccato.staccato.repository.StaccatoRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class StaccatoTest {
    @DisplayName("카테고리 날짜 안에 스타카토 날짜가 포함되면 Staccato를 생성할 수 있다.")
    @Test
    void createStaccato() {
        // given
        Category category = CategoryFixtures.ofDefault()
                .withTerm(LocalDate.of(2024, 1, 1),
                        LocalDate.of(2024, 12, 31)).build();
        LocalDateTime visitedAt = LocalDateTime.of(2024, 6, 1, 0, 0);

        // when & then
        assertDoesNotThrow(() -> Staccato.builder()
                .visitedAt(visitedAt)
                .title("staccatoTitle")
                .placeName("placeName")
                .latitude(BigDecimal.ONE)
                .longitude(BigDecimal.ONE)
                .address("address")
                .category(category)
                .staccatoImages(new StaccatoImages(List.of()))
                .build());
    }

    @DisplayName("카테고리 기간이 없는 경우 스타카토를 날짜 상관없이 생성할 수 있다.")
    @Test
    void createStaccatoInUndefinedDuration() {
        // given
        Category category = CategoryFixtures.ofDefault().build();
        LocalDateTime visitedAt = LocalDateTime.of(2024, 6, 1, 0, 0);

        // when & then
        assertDoesNotThrow(() -> Staccato.builder()
                .visitedAt(visitedAt)
                .title("staccatoTitle")
                .placeName("placeName")
                .latitude(BigDecimal.ONE)
                .longitude(BigDecimal.ONE)
                .address("address")
                .category(category)
                .staccatoImages(new StaccatoImages(List.of()))
                .build());
    }

    @DisplayName("카테고리 날짜 안에 스타카토 날짜가 포함되지 않으면 예외를 발생시킨다.")
    @Test
    void failCreateStaccato() {
        // given
        Category category = CategoryFixtures.ofDefault()
                .withTerm(LocalDate.of(2024, 1, 1),
                        LocalDate.of(2024, 12, 31)).build();
        LocalDateTime visitedAt = LocalDateTime.of(2023, 6, 1, 0, 0);

        // when & then
        assertThatThrownBy(() -> Staccato.builder()
                .visitedAt(visitedAt)
                .title("staccatoTitle")
                .placeName("placeName")
                .latitude(BigDecimal.ONE)
                .longitude(BigDecimal.ONE)
                .address("address")
                .category(category)
                .staccatoImages(new StaccatoImages(List.of()))
                .build())
                .isInstanceOf(StaccatoException.class)
                .hasMessageContaining("카테고리에 포함되지 않는 날짜입니다.");
    }

    @DisplayName("이미지가 있을 경우 첫번째 사진을 썸네일로 반환한다.")
    @Test
    void thumbnail() {
        // given
        Category category = CategoryFixtures.ofDefault().build();
        String thumbnail = "https://example.com/staccatoImage1.jpg";

        Staccato staccato = StaccatoFixtures.ofDefault(category)
                .withStaccatoImages(List.of(thumbnail, "https://example.com/staccatoImage2.jpg")).build();

        // when
        String result = staccato.thumbnailUrl();

        // then
        assertThat(result).isEqualTo(thumbnail);
    }

    @DisplayName("이미지가 없을 경우 null을 썸네일로 반환한다.")
    @Test
    void noThumbnail() {
        // given
        Category category = CategoryFixtures.ofDefault().build();
        Staccato staccato = StaccatoFixtures.ofDefault(category)
                .withStaccatoImages(List.of()).build();

        // when
        String result = staccato.thumbnailUrl();

        // then
        assertThat(result).isNull();
    }

    @Nested
    @Transactional
    @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
    class TouchForWriteTest {
        @Autowired
        private MemberRepository memberRepository;
        @Autowired
        private CategoryRepository categoryRepository;
        @Autowired
        private StaccatoRepository staccatoRepository;
        @PersistenceContext
        private EntityManager entityManager;

        @DisplayName("Staccato 생성 시 Category의 updatedAt이 갱신된다.")
        @Test
        void updateCategoryUpdatedDateWhenStaccatoCreated() {
            // given
            Member member = MemberFixtures.ofDefault().buildAndSave(memberRepository);
            Category category = CategoryFixtures.ofDefault()
                    .withHost(member)
                    .buildAndSave(categoryRepository);
            LocalDateTime beforeCreate = category.getUpdatedAt();

            // when
            StaccatoFixtures.ofDefault(category).buildAndSave(staccatoRepository);
            entityManager.flush();
            entityManager.refresh(category);
            LocalDateTime afterCreate = category.getUpdatedAt();

            // then
            assertThat(afterCreate).isAfter(beforeCreate);
        }

        @DisplayName("Staccato 수정 시 Category의 updatedAt이 갱신된다.")
        @Test
        void updateCategoryUpdatedDateWhenStaccatoUpdated() {
            // given
            Member member = MemberFixtures.ofDefault().buildAndSave(memberRepository);
            Category category = CategoryFixtures.ofDefault()
                    .withHost(member)
                    .buildAndSave(categoryRepository);
            Staccato staccato = StaccatoFixtures.ofDefault(category).buildAndSave(staccatoRepository);
            LocalDateTime beforeUpdate = category.getUpdatedAt();

            // when
            staccato.changeFeeling(Feeling.ANGRY);
            entityManager.flush();
            entityManager.refresh(category);
            LocalDateTime afterUpdate = category.getUpdatedAt();

            // then
            assertThat(afterUpdate).isAfter(beforeUpdate);
        }

        @DisplayName("Staccato 삭제 시 Category의 updatedAt이 갱신된다.")
        @Test
        void updateCategoryUpdatedDateWhenStaccatoDeleted() {
            // given
            Member member = MemberFixtures.ofDefault().buildAndSave(memberRepository);
            Category category = CategoryFixtures.ofDefault()
                    .withHost(member)
                    .buildAndSave(categoryRepository);
            Staccato staccato = StaccatoFixtures.ofDefault(category).buildAndSave(staccatoRepository);
            LocalDateTime beforeDelete = category.getUpdatedAt();

            // when
            staccatoRepository.delete(staccato);
            entityManager.flush();
            entityManager.refresh(category);
            LocalDateTime afterDelete = category.getUpdatedAt();

            // then
            assertThat(afterDelete).isAfter(beforeDelete);
        }
    }

    @DisplayName("스타카토의 색상은 카테고리의 색상을 따른다.")
    @Test
    void getColor() {
        // given
        Member member = MemberFixtures.ofDefault().build();
        Category category = CategoryFixtures.ofDefault()
                .withColor(Color.PINK)
                .withHost(member)
                .build();
        Staccato staccato = StaccatoFixtures.ofDefault(category).build();

        // when
        Color color = staccato.getColor();

        // then
        assertThat(color).isEqualTo(Color.PINK);
    }

    @DisplayName("스타카토의 카테고리를 변경할 수 없는 경우 예외가 발생한다.")
    @Test
    void validateCategoryChangeable() {
        // given
        Member host = MemberFixtures.ofDefault().withNickname("host").build();
        Member guest = MemberFixtures.ofDefault().withNickname("guest").build();
        Category category = CategoryFixtures.ofDefault()
                .withTitle("non-shared")
                .withHost(host)
                .build();
        Category sharedCategory = CategoryFixtures.ofDefault()
                .withTitle("shared")
                .withHost(host)
                .withGuests(List.of(guest))
                .build();
        Staccato staccato = StaccatoFixtures.ofDefault(category).build();
        Staccato sharedStaccato = StaccatoFixtures.ofDefault(sharedCategory).build();

        // when & then
        assertAll(
                () -> assertThatThrownBy(() -> staccato.validateCategoryChangeable(sharedCategory))
                        .isInstanceOf(StaccatoException.class)
                        .hasMessage("개인 카테고리 간에만 스타카토를 옮길 수 있어요."),
                () -> assertThatThrownBy(() -> sharedStaccato.validateCategoryChangeable(category))
                        .isInstanceOf(StaccatoException.class)
                        .hasMessage("개인 카테고리 간에만 스타카토를 옮길 수 있어요.")
        );
    }

    @Nested
    @DisplayName("스타카토의 소유자를 검증한다.")
    class ValidateStaccatoOwner {

        private Member host;
        private Member guest;
        private Category category;
        private Staccato staccato;

        @BeforeEach
        void setUp() {
            host = MemberFixtures.ofDefault().withNickname("host").build();
            guest = MemberFixtures.ofDefault().withNickname("guest").build();
            category = CategoryFixtures.ofDefault()
                    .withHost(host)
                    .withGuests(List.of(guest))
                    .build();
            staccato = StaccatoFixtures.ofDefault(category).build();
        }

        @DisplayName("카테고리의 HOST는 카테고리 안에 있는 스타카토의 소유자이다.")
        @Test
        void successValidateOwnerIfMemberIsCategoryHost() {
            assertDoesNotThrow(() -> staccato.validateOwner(host));
        }

        @DisplayName("카테고리의 GUEST는 카테고리 안에 있는 스타카토의 소유자이다.")
        @Test
        void successValidateOwnerIfMemberIsCategoryGuest() {
            assertDoesNotThrow(() -> staccato.validateOwner(host));
        }

        @DisplayName("카테고리의 함께하는 사람이 아니면 카테고리 안에 있는 스타카토의 소유자가 아니다.")
        @Test
        void failValidateOwnerIfMemberNotInCategory() {
            Member other = MemberFixtures.ofDefault().withNickname("other").build();
            assertThatThrownBy(() -> staccato.validateOwner(other))
                    .isInstanceOf(ForbiddenException.class);
        }
    }

    @DisplayName("Staccato 생성 시 생성자/수정자는 생성한 사람으로 만들어진다.")
    @Test
    void createdAndModifiedBySameWhenCreated() {
        // given
        Member creator = MemberFixtures.ofDefault().withNickname("creator").build();
        Category category = CategoryFixtures.ofDefault()
                .withHost(creator)
                .build();

        // when
        Staccato staccato = Staccato.create(
                LocalDateTime.of(2025, 1, 1, 12, 0),
                "첫 방문",
                "장소명",
                "주소",
                BigDecimal.ONE,
                BigDecimal.ONE,
                List.of("https://image.url/1.jpg"),
                category,
                creator
        );

        // then
        assertAll(
                () -> assertThat(staccato.getCreatedBy()).isEqualTo(creator.getId()),
                () -> assertThat(staccato.getModifiedBy()).isEqualTo(creator.getId())
        );
    }

    @DisplayName("Staccato update 시 createdBy는 변경되지 않는다.")
    @Test
    void createdByDoesNotChangeOnUpdate() {
        // given
        Member creator = MemberFixtures.ofDefault().withNickname("creator").build();
        Member updater = MemberFixtures.ofDefault().withNickname("updater").build();
        Category category = CategoryFixtures.ofDefault().withHost(creator).build();

        Staccato original = StaccatoFixtures.ofDefault(category)
                .withCreator(creator)
                .build();

        Staccato updateData = StaccatoFixtures.ofDefault(category)
                .withCreator(updater)
                .build();

        // when
        original.update(updateData);

        // then
        assertThat(original.getCreatedBy()).isEqualTo(creator.getId());
    }

    @DisplayName("Staccato update 시 modifiedBy는 최근 수정자로 갱신된다.")
    @Test
    void modifiedByChangesOnUpdate() {
        // given
        Member creator = MemberFixtures.ofDefault().withNickname("creator").build();
        Member updater = MemberFixtures.ofDefault().withNickname("updater").build();
        Category category = CategoryFixtures.ofDefault().withHost(creator).build();

        Staccato original = StaccatoFixtures.ofDefault(category)
                .withCreator(creator)
                .build();

        Staccato updateData = StaccatoFixtures.ofDefault(category)
                .withCreator(updater)
                .build();

        // when
        original.update(updateData);

        // then
        assertThat(original.getModifiedBy()).isEqualTo(updater.getId());
    }
}
