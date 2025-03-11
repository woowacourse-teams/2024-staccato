package com.staccato.staccato.repository;

import com.staccato.category.domain.Category;
import com.staccato.category.repository.CategoryMemberRepository;
import com.staccato.staccato.domain.Staccato;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.staccato.RepositoryTest;
import com.staccato.fixture.Member.MemberFixture;
import com.staccato.fixture.category.CategoryFixture;
import com.staccato.fixture.moment.StaccatoFixture;
import com.staccato.member.domain.Member;
import com.staccato.member.repository.MemberRepository;
import com.staccato.category.domain.CategoryMember;
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

    @DisplayName("사용자의 모든 스타카토를 조회한다.")
    @Test
    void findAllByCategory_CategoryMembers_Member() {
        // given
        Member member = memberRepository.save(MemberFixture.create());
        Member anotherMember = memberRepository.save(MemberFixture.create("anotherMember"));
        Category category = categoryRepository.save(
            CategoryFixture.create(LocalDate.of(2023, 12, 31), LocalDate.of(2024, 1, 10)));
        Category category2 = categoryRepository.save(
            CategoryFixture.create(LocalDate.of(2024, 7, 1), LocalDate.of(2024, 7, 10)));
        Category anotherMemberCategory = categoryRepository.save(
            CategoryFixture.create(LocalDate.of(2024, 5, 1), LocalDate.of(2024, 6, 10)));
        categoryMemberRepository.save(new CategoryMember(member, category));
        categoryMemberRepository.save(new CategoryMember(member, category2));
        categoryMemberRepository.save(new CategoryMember(anotherMember, anotherMemberCategory));

        Staccato staccato = staccatoRepository.save(StaccatoFixture.create(
            category, LocalDateTime.of(2023, 12, 31, 22, 20)));
        Staccato staccato1 = staccatoRepository.save(StaccatoFixture.create(
            category, LocalDateTime.of(2024, 1, 1, 22, 20)));
        Staccato staccato2 = staccatoRepository.save(StaccatoFixture.create(
            category, LocalDateTime.of(2024, 1, 1, 22, 21)));
        Staccato anotherStaccato = staccatoRepository.save(
            StaccatoFixture.create(anotherMemberCategory, LocalDateTime.of(2024, 5, 1, 22, 21)));

        // when
        List<Staccato> memberResult = staccatoRepository.findAllByCategory_CategoryMembers_Member(member);
        List<Staccato> anotherMemberResult = staccatoRepository.findAllByCategory_CategoryMembers_Member(anotherMember);

        // then
        assertAll(
                () -> assertThat(memberResult.size()).isEqualTo(3),
                () -> assertThat(memberResult).containsExactlyInAnyOrder(staccato, staccato1,
                    staccato2),
                () -> assertThat(anotherMemberResult.size()).isEqualTo(1),
                () -> assertThat(anotherMemberResult).containsExactlyInAnyOrder(anotherStaccato)
        );
    }

    @DisplayName("특정 카테고리의 id를 가진 모든 스타카토를 삭제한다.")
    @Test
    void deleteAllByCategoryIdInBulk() {
        // given
        Member member = memberRepository.save(MemberFixture.create());
        Category category = categoryRepository.save(
            CategoryFixture.create(LocalDate.of(2023, 12, 31), LocalDate.of(2024, 1, 10)));
        categoryMemberRepository.save(new CategoryMember(member, category));

        Staccato staccato = staccatoRepository.save(StaccatoFixture.create(
            category, LocalDateTime.of(2023, 12, 31, 22, 20)));
        Staccato staccato1 = staccatoRepository.save(StaccatoFixture.create(
            category, LocalDateTime.of(2024, 1, 1, 22, 20)));
        Staccato staccato2 = staccatoRepository.save(StaccatoFixture.create(
            category, LocalDateTime.of(2024, 1, 1, 22, 21)));

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
        Member member = memberRepository.save(MemberFixture.create());
        Category category = categoryRepository.save(
            CategoryFixture.create(LocalDate.of(2023, 12, 31), LocalDate.of(2024, 1, 10)));
        categoryMemberRepository.save(new CategoryMember(member, category));

        Staccato staccato1 = staccatoRepository.save(
            StaccatoFixture.createWithImages(category, LocalDateTime.of(2023, 12, 31, 22, 20), List.of("image1", "image2")));
        Staccato staccato2 = staccatoRepository.save(
            StaccatoFixture.createWithImages(category, LocalDateTime.of(2024, 1, 1, 22, 20), List.of("image1", "image2")));
        Staccato staccato3 = staccatoRepository.save(StaccatoFixture.create(
            category, LocalDateTime.of(2024, 1, 10, 23, 21)));

        // when
        List<Staccato> staccatoes = staccatoRepository.findAllByCategoryIdOrdered(category.getId());

        // then
        assertAll(
                () -> assertThat(staccatoes.size()).isEqualTo(3),
                () -> assertThat(staccatoes).containsExactly(staccato3, staccato2, staccato1)
        );
    }

    @DisplayName("사용자의 스타카토 방문 날짜가 동일하다면, 생성날짜 기준 최신순으로 조회한다.")
    @Test
    void findAllByCategoryIdOrderByCreatedAt() {
        // given
        Member member = memberRepository.save(MemberFixture.create());
        Category category = categoryRepository.save(
            CategoryFixture.create(LocalDate.of(2023, 12, 31), LocalDate.of(2024, 1, 10)));
        categoryMemberRepository.save(new CategoryMember(member, category));

        Staccato staccato1 = staccatoRepository.save(StaccatoFixture.create(
            category, LocalDateTime.of(2024, 1, 10, 23, 21)));
        Staccato staccato2 = staccatoRepository.save(StaccatoFixture.create(
            category, LocalDateTime.of(2024, 1, 10, 23, 21)));

        // when
        List<Staccato> staccatoes = staccatoRepository.findAllByCategoryIdOrdered(category.getId());

        // then
        assertAll(
                () -> assertThat(staccatoes.size()).isEqualTo(2),
                () -> assertThat(staccatoes).containsExactly(staccato2, staccato1)
        );
    }
}
