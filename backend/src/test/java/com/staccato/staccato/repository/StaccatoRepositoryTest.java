package com.staccato.staccato.repository;

import com.staccato.category.domain.Category;
import com.staccato.category.repository.CategoryMemberRepository;
import com.staccato.fixture.category.CategoryFixtures;
import com.staccato.fixture.category.CategoryMemberFixtures;
import com.staccato.fixture.member.MemberFixtures;
import com.staccato.fixture.staccato.StaccatoFixtures;
import com.staccato.staccato.domain.Staccato;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.staccato.RepositoryTest;
import com.staccato.member.domain.Member;
import com.staccato.member.repository.MemberRepository;
import com.staccato.category.repository.CategoryRepository;

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

    @DisplayName("특정 사용자의 모든 스타카토 조회 - 위경도 범위 없음")
    @Test
    void findAllStaccatoByMemberWithoutLocationRange() {
        // given
        Member member = MemberFixtures.defaultMember().buildAndSave(memberRepository);
        Member anotherMember = MemberFixtures.defaultMember().buildAndSave(memberRepository);
        Category category1 = CategoryFixtures.defaultCategory().buildAndSaveWithMember(member, categoryRepository);
        Category category2 = CategoryFixtures.defaultCategory().buildAndSaveWithMember(anotherMember, categoryRepository);

        Staccato staccato1 = StaccatoFixtures.defaultStaccato().withCategory(category1).buildAndSave(staccatoRepository);
        Staccato staccato2 = StaccatoFixtures.defaultStaccato().withCategory(category1).buildAndSave(staccatoRepository);
        Staccato anotherStaccato = StaccatoFixtures.defaultStaccato().withCategory(category2).buildAndSave(staccatoRepository);

        // when
        List<Staccato> result = staccatoRepository.findByMemberAndLocationRange(member, null, null, null, null);

        // then
        assertThat(result).hasSize(2).containsExactlyInAnyOrder(staccato1, staccato2);
    }

    @DisplayName("특정 사용자의 스타카토 조회 - 위경도 범위 내 (경계 포함)")
    @Test
    void findAllStaccatoByMemberWithLocationRangeIncludingBoundaries() {
        // given
        Member member = MemberFixtures.defaultMember().buildAndSave(memberRepository);
        Member anotherMember = MemberFixtures.defaultMember().buildAndSave(memberRepository);
        Category category1 = CategoryFixtures.defaultCategory().buildAndSave(categoryRepository);

        CategoryMemberFixtures.defaultCategoryMember().withMember(member).withCategory(category1).buildAndSave(categoryMemberRepository);
        CategoryMemberFixtures.defaultCategoryMember().withMember(anotherMember).withCategory(category1).buildAndSave(categoryMemberRepository);

        Staccato inside1 = StaccatoFixtures.defaultStaccato()
                .withCategory(category1)
                .withSpot(new BigDecimal("37.5"), new BigDecimal("127.0"))
                .buildAndSave(staccatoRepository);
        Staccato inside2 = StaccatoFixtures.defaultStaccato()
                .withCategory(category1)
                .withSpot(new BigDecimal("37.4"), new BigDecimal("127.1"))
                .buildAndSave(staccatoRepository);
        Staccato inside3 = StaccatoFixtures.defaultStaccato()
                .withCategory(category1)
                .withSpot(new BigDecimal("37.45"), new BigDecimal("127.05"))
                .buildAndSave(staccatoRepository);
        Staccato outsideLat = StaccatoFixtures.defaultStaccato()
                .withCategory(category1)
                .withSpot(new BigDecimal("37.6"), new BigDecimal("127.0"))
                .buildAndSave(staccatoRepository);
        Staccato outsideLng = StaccatoFixtures.defaultStaccato()
                .withCategory(category1)
                .withSpot(new BigDecimal("37.5"), new BigDecimal("127.2"))
                .buildAndSave(staccatoRepository);

        // when
        BigDecimal minLat = new BigDecimal("37.4");
        BigDecimal maxLat = new BigDecimal("37.5");
        BigDecimal minLng = new BigDecimal("127.0");
        BigDecimal maxLng = new BigDecimal("127.1");

        List<Staccato> result = staccatoRepository.findByMemberAndLocationRange(
                member, minLat, maxLat, minLng, maxLng
        );

        // then
        assertThat(result).hasSize(3).containsExactlyInAnyOrder(inside1, inside2, inside3);
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
}
