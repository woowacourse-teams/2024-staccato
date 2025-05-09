package com.staccato.staccato.domain;

import com.staccato.category.domain.Category;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import com.staccato.category.domain.Color;
import com.staccato.exception.StaccatoException;
import com.staccato.fixture.category.CategoryFixtures;
import com.staccato.fixture.member.MemberFixtures;
import com.staccato.fixture.staccato.StaccatoFixtures;
import com.staccato.member.domain.Member;
import com.staccato.member.repository.MemberRepository;
import com.staccato.category.repository.CategoryRepository;
import com.staccato.staccato.repository.StaccatoRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class StaccatoTest {
    @DisplayName("카테고리 날짜 안에 스타카토 날짜가 포함되면 Staccato를 생성할 수 있다.")
    @Test
    void createStaccato() {
        // given
        Category category = CategoryFixtures.defaultCategory()
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
        Category category = CategoryFixtures.defaultCategory()
                .withTerm(null, null).build();
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

    @DisplayName("스타카토 생성 시 title의 앞 뒤 공백이 제거된다.")
    @Test
    void trimPlaceName() {
        // given
        Category category = CategoryFixtures.defaultCategory().build();
        LocalDateTime visitedAt = LocalDateTime.of(2024, 6, 1, 0, 0);
        String title = " staccatoTitle ";
        String expectedTitle = "staccatoTitle";

        // when
        Staccato staccato = Staccato.builder()
                .visitedAt(visitedAt)
                .title(title)
                .latitude(BigDecimal.ONE)
                .longitude(BigDecimal.ONE)
                .placeName("placeName")
                .address("address")
                .category(category)
                .staccatoImages(new StaccatoImages(List.of()))
                .build();

        // then
        assertThat(staccato.getTitle()).isEqualTo(expectedTitle);
    }

    @DisplayName("카테고리 날짜 안에 스타카토 날짜가 포함되지 않으면 예외를 발생시킨다.")
    @Test
    void failCreateStaccato() {
        // given
        Category category = CategoryFixtures.defaultCategory()
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
    void thumbnail(){
        // given
        Category category = CategoryFixtures.defaultCategory().build();
        String thumbnail = "https://example.com/staccatoImage1.jpg";

        Staccato staccato = StaccatoFixtures.defaultStaccato()
                .withCategory(category)
                .withStaccatoImages(List.of(thumbnail, "https://example.com/staccatoImage2.jpg")).build();

        // when
        String result = staccato.thumbnailUrl();

        // then
        assertThat(result).isEqualTo(thumbnail);
    }

    @DisplayName("이미지가 없을 경우 null을 썸네일로 반환한다.")
    @Test
    void noThumbnail(){
        // given
        Category category = CategoryFixtures.defaultCategory().build();
        Staccato staccato = StaccatoFixtures.defaultStaccato()
                .withCategory(category)
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
            Member member = MemberFixtures.defaultMember().buildAndSave(memberRepository);
            Category category = CategoryFixtures.defaultCategory()
                    .buildAndSaveWithMember(member, categoryRepository);
            LocalDateTime beforeCreate = category.getUpdatedAt();

            // when
            StaccatoFixtures.defaultStaccato()
                    .withCategory(category).buildAndSave(staccatoRepository);
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
            Member member = MemberFixtures.defaultMember().buildAndSave(memberRepository);
            Category category = CategoryFixtures.defaultCategory()
                    .buildAndSaveWithMember(member, categoryRepository);
            Staccato staccato = StaccatoFixtures.defaultStaccato()
                    .withCategory(category).buildAndSave(staccatoRepository);
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
            Member member = MemberFixtures.defaultMember().buildAndSave(memberRepository);
            Category category = CategoryFixtures.defaultCategory()
                    .buildAndSaveWithMember(member, categoryRepository);
            Staccato staccato = StaccatoFixtures.defaultStaccato()
                    .withCategory(category).buildAndSave(staccatoRepository);
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
        Member member = MemberFixtures.defaultMember().build();
        Category category = CategoryFixtures.defaultCategory()
                .withColor(Color.PINK)
                .buildWithMember(member);
        Staccato staccato = StaccatoFixtures.defaultStaccato()
                .withCategory(category).build();

        // when
        Color color = staccato.getColor();

        // then
        assertThat(color).isEqualTo(Color.PINK);
    }
}
