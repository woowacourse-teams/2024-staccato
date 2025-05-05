package com.staccato.category.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.staccato.ServiceSliceTest;
import com.staccato.fixture.category.CategoryFixtures;
import com.staccato.fixture.member.MemberFixtures;
import com.staccato.member.domain.Member;
import com.staccato.member.repository.MemberRepository;
import com.staccato.category.domain.Category;
import com.staccato.category.repository.CategoryRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class CategoryFilterTest extends ServiceSliceTest {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    @DisplayName("필터링명이 주어졌을 때 대소문자 구분 없이 CategoryFilter을 반환한다.")
    @Test
    void findByNameWithValidFilter() {
        // when
        List<CategoryFilter> result = CategoryFilter.findAllByName(List.of("without_term"));

        // then
        assertAll(
                () -> assertThat(result).hasSize(1),
                () -> assertThat(result.get(0)).isEqualTo(CategoryFilter.NO_TERM)
        );
    }

    @DisplayName("필터링명이 주어졌을 때 유효한 CategoryFilter 목록만 반환한다.")
    @Test
    void findAllByNameIfOnlyValid() {
        // when
        List<CategoryFilter> result = CategoryFilter.findAllByName(List.of("invalid", "with_term"));

        // then
        assertAll(
                () -> assertThat(result).hasSize(1),
                () -> assertThat(result.get(0)).isEqualTo(CategoryFilter.TERM)
        );
    }

    @DisplayName("필터링명이 중복으로 주어졌을 때 중복 없이 CategoryFilter 목록을 반환한다.")
    @Test
    void findAllByNameDistinct() {
        // when
        List<CategoryFilter> result = CategoryFilter.findAllByName(List.of("WITH_TERM", "with_term"));

        // then
        assertAll(
                () -> assertThat(result).hasSize(1),
                () -> assertThat(result.get(0)).isEqualTo(CategoryFilter.TERM)
        );
    }

    @DisplayName("유효하지 않거나 null인 정렬명이 주어졌을 때 빈 값을 반환한다.")
    @Test
    void findByNameWithInvalid() {
        // when
        List<CategoryFilter> result = CategoryFilter.findAllByName(List.of("invalid"));

        // then
        assertThat(result).isEmpty();
    }

    @DisplayName("기간이 있는 카테고리 목록만 조회된다.")
    @Test
    void readAllCategoriesWithTerm() {
        // given
        Member member = MemberFixtures.defaultMember().buildAndSave(memberRepository);
        Category category1 = CategoryFixtures.defaultCategory()
                .withTitle("first")
                .withTerm(null, null)
                .buildAndSaveWithHostMember(member, categoryRepository);
        Category category2 = CategoryFixtures.defaultCategory()
                .withTitle("second")
                .withTerm(LocalDate.of(2024, 1, 1),
                        LocalDate.of(2024, 12, 31))
                .buildAndSaveWithHostMember(member, categoryRepository);
        List<Category> categories = new ArrayList<>();
        categories.add(category1);
        categories.add(category2);

        // when
        List<Category> result = CategoryFilter.TERM.apply(categories);

        // then
        assertAll(
                () -> assertThat(result).hasSize(1),
                () -> assertThat(result.get(0).getTitle()).isEqualTo(category2.getTitle())
        );
    }

    @DisplayName("기간이 없는 카테고리 목록만 조회된다.")
    @Test
    void readAllCategoriesWithoutTerm() {
        // given
        Member member = MemberFixtures.defaultMember().buildAndSave(memberRepository);
        Category category1 = CategoryFixtures.defaultCategory()
                .withTitle("first")
                .withTerm(null, null)
                .buildAndSaveWithHostMember(member, categoryRepository);
        Category category2 = CategoryFixtures.defaultCategory()
                .withTitle("second")
                .withTerm(LocalDate.of(2024, 1, 1),
                        LocalDate.of(2024, 12, 31))
                .buildAndSaveWithHostMember(member, categoryRepository);
        List<Category> categories = new ArrayList<>();
        categories.add(category1);
        categories.add(category2);

        // when
        List<Category> result = CategoryFilter.NO_TERM.apply(categories);

        // then
        assertAll(
            () -> assertThat(result).hasSize(1),
            () -> assertThat(result.get(0).getTitle()).isEqualTo(category1.getTitle())
        );
    }
}
