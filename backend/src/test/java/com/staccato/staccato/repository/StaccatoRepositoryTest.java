package com.staccato.staccato.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import com.staccato.RepositoryTest;
import com.staccato.category.domain.Category;
import com.staccato.category.repository.CategoryMemberRepository;
import com.staccato.category.repository.CategoryRepository;
import com.staccato.fixture.category.CategoryFixtures;
import com.staccato.fixture.category.CategoryMemberFixtures;
import com.staccato.fixture.member.MemberFixtures;
import com.staccato.fixture.staccato.StaccatoFixtures;
import com.staccato.member.domain.Member;
import com.staccato.member.repository.MemberRepository;
import com.staccato.staccato.domain.Staccato;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class StaccatoRepositoryTest extends RepositoryTest {
    @Autowired
    private StaccatoRepository staccatoRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private CategoryMemberRepository categoryMemberRepository;
    @PersistenceContext
    private EntityManager entityManager;

    @Nested
    @DisplayName("사용자(member)의 스타카토 목록 조회")
    class FindAllStaccatosBy {
        private static final BigDecimal MIN_LATITUDE = new BigDecimal("37.4");
        private static final BigDecimal MAX_LATITUDE = new BigDecimal("37.5");
        private static final BigDecimal MIN_LONGITUDE = new BigDecimal("127.1");
        private static final BigDecimal MAX_LONGITUDE = new BigDecimal("127.2");

        Member member;
        Category category1ByMember;
        Category category2ByMember;
        Staccato staccatoInCategory1;
        Staccato staccatoInCategory2;

        @BeforeEach
        void init() {
            member = MemberFixtures.defaultMember().buildAndSave(memberRepository);
            category1ByMember = CategoryFixtures.defaultCategory()
                    .withTitle("category1")
                    .withHost(member)
                    .buildAndSave(categoryRepository);
            category2ByMember = CategoryFixtures.defaultCategory()
                    .withTitle("category2")
                    .withHost(member)
                    .buildAndSave(categoryRepository);
            staccatoInCategory1 = StaccatoFixtures.defaultStaccato()
                    .withSpot(MIN_LATITUDE, MAX_LONGITUDE)
                    .withCategory(category1ByMember)
                    .buildAndSave(staccatoRepository);
            staccatoInCategory2 = StaccatoFixtures.defaultStaccato()
                    .withSpot(MAX_LATITUDE, MIN_LONGITUDE)
                    .withCategory(category2ByMember)
                    .buildAndSave(staccatoRepository);
        }

        @Nested
        @DisplayName("findByMemberAndLocationRange()")
        class FindByMemberAndLocationRange {
            @DisplayName("아무 조건 없음 (staccatoInCategory1, staccatoInCategory2)")
            @Test
            void findAllStaccatoByMemberWithoutAnyCondition() {
                // given
                Member anotherMember = MemberFixtures.defaultMember().buildAndSave(memberRepository);
                Category anotherCategory = CategoryFixtures.defaultCategory()
                        .withHost(anotherMember)
                        .buildAndSave(categoryRepository);
                Staccato anotherStaccato = StaccatoFixtures.defaultStaccato()
                        .withCategory(anotherCategory)
                        .buildAndSave(staccatoRepository);

                // when
                List<Staccato> result = staccatoRepository.findByMemberAndLocationRange(member, null, null, null, null);

                // then
                assertThat(result).hasSize(2).containsExactlyInAnyOrder(staccatoInCategory1, staccatoInCategory2)
                        .doesNotContain(anotherStaccato);
            }

            @DisplayName("위경도 범위 내 (staccatoInCategory1, staccatoInCategory2)")
            @Test
            void findAllStaccatoByMemberWithLocationRange() {
                // given
                BigDecimal threshold = new BigDecimal("0.01");
                Staccato underMinLatitude = StaccatoFixtures.defaultStaccato()
                        .withCategory(category1ByMember)
                        .withSpot(MIN_LATITUDE.subtract(threshold), MAX_LONGITUDE)
                        .buildAndSave(staccatoRepository);
                Staccato overMaxLatitude = StaccatoFixtures.defaultStaccato()
                        .withCategory(category1ByMember)
                        .withSpot(MAX_LATITUDE.add(threshold), MIN_LONGITUDE)
                        .buildAndSave(staccatoRepository);
                Staccato underMinLongitude = StaccatoFixtures.defaultStaccato()
                        .withCategory(category1ByMember)
                        .withSpot(MAX_LATITUDE, MIN_LONGITUDE.subtract(threshold))
                        .buildAndSave(staccatoRepository);
                Staccato overMaxLongitude = StaccatoFixtures.defaultStaccato()
                        .withCategory(category1ByMember)
                        .withSpot(MIN_LATITUDE, MAX_LONGITUDE.add(threshold))
                        .buildAndSave(staccatoRepository);

                // when
                List<Staccato> result = staccatoRepository.findByMemberAndLocationRange(
                        member, MIN_LATITUDE, MAX_LATITUDE, MIN_LONGITUDE, MAX_LONGITUDE
                );

                // then
                assertThat(result).hasSize(2).containsExactlyInAnyOrder(staccatoInCategory1, staccatoInCategory2)
                        .doesNotContain(underMinLatitude, overMaxLatitude, underMinLongitude, overMaxLongitude);
            }
        }

        @Nested
        @DisplayName("findByMemberAndLocationRangeAndCategory()")
        class FindByMemberAndLocationRangeAndCategory {
            @DisplayName("특정 카테고리(category1ByMember)에 속한 모든 스타카토 (staccatoInCategory1)")
            @Test
            void findAllStaccatoByMemberWithCategory() {
                // when
                List<Staccato> result = staccatoRepository.findByMemberAndLocationRangeAndCategory(member, null, null, null, null, category1ByMember.getId());

                // then
                assertThat(result).hasSize(1)
                        .containsExactlyInAnyOrder(staccatoInCategory1)
                        .doesNotContain(staccatoInCategory2);
            }

            @DisplayName("특정 카테고리(category1ByMember)에 속하면서 위경도 범위 내 모든 스타카토 (staccatoInCategory1)")
            @Test
            void findAllStaccatoByMemberWithCategoryAndInRange() {
                // given
                BigDecimal threshold = new BigDecimal("0.01");
                Staccato underMinLatitude = StaccatoFixtures.defaultStaccato()
                        .withCategory(category1ByMember)
                        .withSpot(MIN_LATITUDE.subtract(threshold), MAX_LONGITUDE)
                        .buildAndSave(staccatoRepository);
                Staccato overMaxLatitude = StaccatoFixtures.defaultStaccato()
                        .withCategory(category1ByMember)
                        .withSpot(MAX_LATITUDE.add(threshold), MIN_LONGITUDE)
                        .buildAndSave(staccatoRepository);
                Staccato underMinLongitude = StaccatoFixtures.defaultStaccato()
                        .withCategory(category1ByMember)
                        .withSpot(MAX_LATITUDE, MIN_LONGITUDE.subtract(threshold))
                        .buildAndSave(staccatoRepository);
                Staccato overMaxLongitude = StaccatoFixtures.defaultStaccato()
                        .withCategory(category1ByMember)
                        .withSpot(MIN_LATITUDE, MAX_LONGITUDE.add(threshold))
                        .buildAndSave(staccatoRepository);

                // when
                List<Staccato> result = staccatoRepository.findByMemberAndLocationRangeAndCategory(
                        member, MIN_LATITUDE, MAX_LATITUDE, MIN_LONGITUDE, MAX_LONGITUDE, category1ByMember.getId());

                // then
                assertThat(result).hasSize(1)
                        .containsExactlyInAnyOrder(staccatoInCategory1)
                        .doesNotContain(staccatoInCategory2, underMinLatitude, overMaxLatitude, underMinLongitude, overMaxLongitude);
            }
        }
    }

    @DisplayName("특정 카테고리의 id를 가진 모든 스타카토를 삭제한다.")
    @Test
    void deleteAllByCategoryIdInBulk() {
        // given
        Member member = MemberFixtures.defaultMember().buildAndSave(memberRepository);
        Category category = CategoryFixtures.defaultCategory().buildAndSave(categoryRepository);
        CategoryMemberFixtures.defaultCategoryMember()
                .withMember(member)
                .withCategory(category).buildAndSave(categoryMemberRepository);

        Staccato staccato = StaccatoFixtures.defaultStaccato()
                .withCategory(category).buildAndSave(staccatoRepository);
        Staccato staccato1 = StaccatoFixtures.defaultStaccato()
                .withCategory(category).buildAndSave(staccatoRepository);
        Staccato staccato2 = StaccatoFixtures.defaultStaccato()
                .withCategory(category).buildAndSave(staccatoRepository);

        // when
        staccatoRepository.deleteAllByCategoryIdInBulk(category.getId());
        entityManager.flush();
        entityManager.clear();

        // then
        assertAll(
                () -> assertThat(staccatoRepository.findById(staccato.getId()).isEmpty()).isTrue(),
                () -> assertThat(staccatoRepository.findById(staccato1.getId()).isEmpty()).isTrue(),
                () -> assertThat(staccatoRepository.findById(staccato2.getId()).isEmpty()).isTrue(),
                () -> assertThat(staccatoRepository.findAllByCategoryId(category.getId())).isEqualTo(List.of())
        );
    }

    @DisplayName("사용자의 특정 카테고리에 해당하는 모든 스타카토를 최신순으로 조회한다.")
    @Test
    void findAllByCategoryIdOrderByVisitedAt() {
        // given
        Member member = MemberFixtures.defaultMember().buildAndSave(memberRepository);
        Category category = CategoryFixtures.defaultCategory().buildAndSave(categoryRepository);
        CategoryMemberFixtures.defaultCategoryMember()
                .withMember(member)
                .withCategory(category).buildAndSave(categoryMemberRepository);

        Staccato staccato1 = StaccatoFixtures.defaultStaccato()
                .withVisitedAt(LocalDateTime.of(2024, 6, 1, 0, 0))
                .withCategory(category).buildAndSave(staccatoRepository);
        Staccato staccato2 = StaccatoFixtures.defaultStaccato()
                .withVisitedAt(LocalDateTime.of(2024, 6, 2, 0, 0))
                .withCategory(category).buildAndSave(staccatoRepository);
        Staccato staccato3 = StaccatoFixtures.defaultStaccato()
                .withVisitedAt(LocalDateTime.of(2024, 6, 3, 0, 0))
                .withCategory(category).buildAndSave(staccatoRepository);

        // when
        List<Staccato> staccatos = staccatoRepository.findAllByCategoryIdOrdered(category.getId());

        // then
        assertAll(
                () -> assertThat(staccatos.size()).isEqualTo(3),
                () -> assertThat(staccatos).containsExactly(staccato3, staccato2, staccato1)
        );
    }

    @DisplayName("사용자의 스타카토 방문 날짜가 동일하다면, 생성날짜 기준 최신순으로 조회한다.")
    @Test
    void findAllByCategoryIdOrderByCreatedAt() {
        // given
        Member member = MemberFixtures.defaultMember().buildAndSave(memberRepository);
        Category category = CategoryFixtures.defaultCategory().buildAndSave(categoryRepository);
        CategoryMemberFixtures.defaultCategoryMember()
                .withMember(member)
                .withCategory(category).buildAndSave(categoryMemberRepository);

        Staccato staccato1 = StaccatoFixtures.defaultStaccato()
                .withVisitedAt(LocalDateTime.of(2024, 6, 1, 0, 0))
                .withCategory(category).buildAndSave(staccatoRepository);
        Staccato staccato2 = StaccatoFixtures.defaultStaccato()
                .withVisitedAt(LocalDateTime.of(2024, 6, 1, 0, 0))
                .withCategory(category).buildAndSave(staccatoRepository);

        // when
        List<Staccato> staccatos = staccatoRepository.findAllByCategoryIdOrdered(category.getId());

        // then
        assertAll(
                () -> assertThat(staccatos.size()).isEqualTo(2),
                () -> assertThat(staccatos).containsExactly(staccato2, staccato1)
        );
    }

    @DisplayName("카테고리에 속한 스타카토의 개수을 조회한다.")
    @ParameterizedTest
    @ValueSource(ints = {0, 2})
    void countAllByCategoryWhenZero(int staccatoCount) {
        //given
        Member host = MemberFixtures.defaultMember().withNickname("host").buildAndSave(memberRepository);
        Category category = CategoryFixtures.defaultCategory()
                .withHost(host)
                .buildAndSave(categoryRepository);
        for (int count = 1; count <= staccatoCount; count++) {
            StaccatoFixtures.defaultStaccato()
                    .withCategory(category)
                    .buildAndSave(staccatoRepository);
        }

        // when
        long result = staccatoRepository.countAllByCategoryId(category.getId());

        // then
        assertThat(result).isEqualTo(result);
    }
}
