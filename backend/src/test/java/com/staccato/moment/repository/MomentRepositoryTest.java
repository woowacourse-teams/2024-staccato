package com.staccato.moment.repository;

import com.staccato.category.domain.Category;
import com.staccato.category.repository.CategoryMemberRepository;
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
import com.staccato.fixture.moment.MomentFixture;
import com.staccato.member.domain.Member;
import com.staccato.member.repository.MemberRepository;
import com.staccato.category.domain.CategoryMember;
import com.staccato.category.repository.CategoryRepository;
import com.staccato.moment.domain.Moment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class MomentRepositoryTest extends RepositoryTest {
    @Autowired
    private MomentRepository momentRepository;
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

        Moment moment = momentRepository.save(MomentFixture.create(
            category, LocalDateTime.of(2023, 12, 31, 22, 20)));
        Moment moment1 = momentRepository.save(MomentFixture.create(
            category, LocalDateTime.of(2024, 1, 1, 22, 20)));
        Moment moment2 = momentRepository.save(MomentFixture.create(
            category, LocalDateTime.of(2024, 1, 1, 22, 21)));
        Moment anotherMoment = momentRepository.save(MomentFixture.create(anotherMemberCategory, LocalDateTime.of(2024, 5, 1, 22, 21)));

        // when
        List<Moment> memberResult = momentRepository.findAllByCategory_CategoryMembers_Member(member);
        List<Moment> anotherMemberResult = momentRepository.findAllByCategory_CategoryMembers_Member(anotherMember);

        // then
        assertAll(
                () -> assertThat(memberResult.size()).isEqualTo(3),
                () -> assertThat(memberResult).containsExactlyInAnyOrder(moment, moment1, moment2),
                () -> assertThat(anotherMemberResult.size()).isEqualTo(1),
                () -> assertThat(anotherMemberResult).containsExactlyInAnyOrder(anotherMoment)
        );
    }

    @DisplayName("특정 추억의 id를 가진 모든 스타카토를 삭제한다.")
    @Test
    void deleteAllByCategoryIdInBulk() {
        // given
        Member member = memberRepository.save(MemberFixture.create());
        Category category = categoryRepository.save(
            CategoryFixture.create(LocalDate.of(2023, 12, 31), LocalDate.of(2024, 1, 10)));
        categoryMemberRepository.save(new CategoryMember(member, category));

        Moment moment = momentRepository.save(MomentFixture.create(
            category, LocalDateTime.of(2023, 12, 31, 22, 20)));
        Moment moment1 = momentRepository.save(MomentFixture.create(
            category, LocalDateTime.of(2024, 1, 1, 22, 20)));
        Moment moment2 = momentRepository.save(MomentFixture.create(
            category, LocalDateTime.of(2024, 1, 1, 22, 21)));

        // when
        momentRepository.deleteAllByCategoryIdInBulk(category.getId());
        entityManager.flush();
        entityManager.clear();

        // then
        assertAll(
                () -> assertThat(momentRepository.findById(moment.getId()).isEmpty()).isTrue(),
                () -> assertThat(momentRepository.findById(moment1.getId()).isEmpty()).isTrue(),
                () -> assertThat(momentRepository.findById(moment2.getId()).isEmpty()).isTrue(),
                () -> assertThat(momentRepository.findAllByCategoryId(category.getId())).isEqualTo(List.of())
        );
    }

    @DisplayName("사용자의 특정 추억에 해당하는 모든 스타카토를 최신순으로 조회한다.")
    @Test
    void findAllByCategoryIdOrderByVisitedAt() {
        // given
        Member member = memberRepository.save(MemberFixture.create());
        Category category = categoryRepository.save(
            CategoryFixture.create(LocalDate.of(2023, 12, 31), LocalDate.of(2024, 1, 10)));
        categoryMemberRepository.save(new CategoryMember(member, category));

        Moment moment1 = momentRepository.save(MomentFixture.createWithImages(category, LocalDateTime.of(2023, 12, 31, 22, 20), List.of("image1", "image2")));
        Moment moment2 = momentRepository.save(MomentFixture.createWithImages(category, LocalDateTime.of(2024, 1, 1, 22, 20), List.of("image1", "image2")));
        Moment moment3 = momentRepository.save(MomentFixture.create(
            category, LocalDateTime.of(2024, 1, 10, 23, 21)));

        // when
        List<Moment> moments = momentRepository.findAllByCategoryIdOrdered(category.getId());

        // then
        assertAll(
                () -> assertThat(moments.size()).isEqualTo(3),
                () -> assertThat(moments).containsExactly(moment3, moment2, moment1)
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

        Moment moment1 = momentRepository.save(MomentFixture.create(
            category, LocalDateTime.of(2024, 1, 10, 23, 21)));
        Moment moment2 = momentRepository.save(MomentFixture.create(
            category, LocalDateTime.of(2024, 1, 10, 23, 21)));

        // when
        List<Moment> moments = momentRepository.findAllByCategoryIdOrdered(category.getId());

        // then
        assertAll(
                () -> assertThat(moments.size()).isEqualTo(2),
                () -> assertThat(moments).containsExactly(moment2, moment1)
        );
    }
}
