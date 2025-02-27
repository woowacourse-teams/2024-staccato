package com.staccato.moment.domain;

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
import com.staccato.exception.StaccatoException;
import com.staccato.fixture.Member.MemberFixture;
import com.staccato.fixture.category.CategoryFixture;
import com.staccato.fixture.moment.MomentFixture;
import com.staccato.member.domain.Member;
import com.staccato.member.repository.MemberRepository;
import com.staccato.category.repository.CategoryRepository;
import com.staccato.moment.repository.MomentRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MomentTest {
    @DisplayName("추억 날짜 안에 스타카토 날짜가 포함되면 Moment을 생성할 수 있다.")
    @Test
    void createMoment() {
        // given
        Category category = CategoryFixture.create(LocalDate.now(), LocalDate.now().plusDays(1));
        LocalDateTime visitedAt = LocalDateTime.now().plusDays(1);

        // when & then
        assertThatCode(() -> Moment.builder()
                .visitedAt(visitedAt)
                .title("staccatoTitle")
                .placeName("placeName")
                .latitude(BigDecimal.ONE)
                .longitude(BigDecimal.ONE)
                .address("address")
                .category(category)
                .momentImages(new MomentImages(List.of()))
                .build())
                .doesNotThrowAnyException();
    }

    @DisplayName("추억 기간이 없는 경우 스타카토를 날짜 상관없이 생성할 수 있다.")
    @Test
    void createMomentInUndefinedDuration() {
        // given
        Category category = CategoryFixture.create(null, null);
        LocalDateTime visitedAt = LocalDateTime.now().plusDays(1);

        // when & then
        assertThatCode(() -> Moment.builder()
                .visitedAt(visitedAt)
                .title("staccatoTitle")
                .placeName("placeName")
                .latitude(BigDecimal.ONE)
                .longitude(BigDecimal.ONE)
                .address("address")
                .category(category)
                .momentImages(new MomentImages(List.of()))
                .build())
                .doesNotThrowAnyException();
    }

    @DisplayName("스타카토 생성 시 title의 앞 뒤 공백이 제거된다.")
    @Test
    void trimPlaceName() {
        // given
        Category category = CategoryFixture.create(LocalDate.now(), LocalDate.now().plusDays(1));
        LocalDateTime visitedAt = LocalDateTime.now().plusDays(1);
        String title = " staccatoTitle ";
        String expectedTitle = "staccatoTitle";

        // when
        Moment moment = Moment.builder()
                .visitedAt(visitedAt)
                .title(title)
                .latitude(BigDecimal.ONE)
                .longitude(BigDecimal.ONE)
                .placeName("placeName")
                .address("address")
                .category(category)
                .momentImages(new MomentImages(List.of()))
                .build();

        // then
        assertThat(moment.getTitle()).isEqualTo(expectedTitle);
    }

    @DisplayName("추억 날짜 안에 스타카토 날짜가 포함되지 않으면 예외를 발생시킨다.")
    @ValueSource(longs = {-1, 2})
    @ParameterizedTest
    void failCreateMoment(long plusDays) {
        // given
        Category category = CategoryFixture.create(LocalDate.now(), LocalDate.now().plusDays(1));
        LocalDateTime visitedAt = LocalDateTime.now().plusDays(plusDays);

        // when & then
        assertThatThrownBy(() -> Moment.builder()
                .visitedAt(visitedAt)
                .title("staccatoTitle")
                .placeName("placeName")
                .latitude(BigDecimal.ONE)
                .longitude(BigDecimal.ONE)
                .address("address")
                .category(category)
                .momentImages(new MomentImages(List.of()))
                .build())
                .isInstanceOf(StaccatoException.class)
                .hasMessageContaining("추억에 포함되지 않는 날짜입니다.");
    }

    @DisplayName("이미지가 있을 경우 첫번째 사진을 썸네일로 반환한다.")
    @Test
    void thumbnail(){
        // given
        Category category = CategoryFixture.create(LocalDate.now(), LocalDate.now().plusDays(1));
        String thumbnail = "1.png";
        Moment moment = MomentFixture.createWithImages(
            category, LocalDateTime.now(), List.of(thumbnail, "2.png"));

        // when
        String result = moment.thumbnailUrl();

        // then
        assertThat(result).isEqualTo(thumbnail);
    }

    @DisplayName("이미지가 없을 경우 null을 썸네일로 반환한다.")
    @Test
    void noThumbnail(){
        // given
        Category category = CategoryFixture.create(LocalDate.now(), LocalDate.now().plusDays(1));
        Moment moment = MomentFixture.createWithImages(category, LocalDateTime.now(), List.of());

        // when
        String result = moment.thumbnailUrl();

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
        private MomentRepository momentRepository;
        @PersistenceContext
        private EntityManager entityManager;

        @DisplayName("Moment 생성 시 Category의 updatedAt이 갱신된다.")
        @Test
        void updateCategoryUpdatedDateWhenMomentCreated() {
            // given
            Member member = memberRepository.save(MemberFixture.create());
            Category category = categoryRepository.save(CategoryFixture.createWithMember(member));
            LocalDateTime beforeCreate = category.getUpdatedAt();

            // when
            momentRepository.save(MomentFixture.create(category));
            entityManager.flush();
            entityManager.refresh(category);
            LocalDateTime afterCreate = category.getUpdatedAt();

            // then
            assertThat(afterCreate).isAfter(beforeCreate);
        }

        @DisplayName("Moment 수정 시 Category의 updatedAt이 갱신된다.")
        @Test
        void updateCategoryUpdatedDateWhenMomentUpdated() {
            // given
            Member member = memberRepository.save(MemberFixture.create());
            Category category = categoryRepository.save(CategoryFixture.createWithMember(member));
            Moment moment = momentRepository.save(MomentFixture.create(category));
            LocalDateTime beforeUpdate = category.getUpdatedAt();

            // when
            moment.changeFeeling(Feeling.ANGRY);
            entityManager.flush();
            entityManager.refresh(category);
            LocalDateTime afterUpdate = category.getUpdatedAt();

            // then
            assertThat(afterUpdate).isAfter(beforeUpdate);
        }

        @DisplayName("Moment 삭제 시 Category의 updatedAt이 갱신된다.")
        @Test
        void updateCategoryUpdatedDateWhenMomentDeleted() {
            // given
            Member member = memberRepository.save(MemberFixture.create());
            Category category = categoryRepository.save(CategoryFixture.createWithMember(member));
            Moment moment = momentRepository.save(MomentFixture.create(category));
            LocalDateTime beforeDelete = category.getUpdatedAt();

            // when
            momentRepository.delete(moment);
            entityManager.flush();
            entityManager.refresh(category);
            LocalDateTime afterDelete = category.getUpdatedAt();

            // then
            assertThat(afterDelete).isAfter(beforeDelete);
        }
    }
}
