package com.staccato.category.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.staccato.ServiceSliceTest;
import com.staccato.fixture.Member.MemberFixture;
import com.staccato.fixture.category.CategoryFixture;
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
        List<CategoryFilter> result = CategoryFilter.findAllByName(List.of("term"));

        // then
        assertAll(
                () -> assertThat(result).hasSize(1),
                () -> assertThat(result.get(0)).isEqualTo(CategoryFilter.TERM)
        );
    }

    @DisplayName("필터링명이 주어졌을 때 유효한 CategoryFilter 목록만 반환한다.")
    @Test
    void findAllByNameIfOnlyValid() {
        // when
        List<CategoryFilter> result = CategoryFilter.findAllByName(List.of("invalid", "term"));

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
        List<CategoryFilter> result = CategoryFilter.findAllByName(List.of("TERM", "term"));

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
    void readAllMemoriesWithTerm() {
        // given
        Member member = memberRepository.save(MemberFixture.create());
        Category category = categoryRepository.save(CategoryFixture.createWithMember("first", member));
        Category category2 = categoryRepository.save(
            CategoryFixture.createWithMember(LocalDate.now(), LocalDate.now()
                .plusDays(3), member));
        List<Category> memories = new ArrayList<>();
        memories.add(category);
        memories.add(category2);

        // when
        List<Category> result = CategoryFilter.TERM.apply(memories);

        // then
        assertAll(
                () -> assertThat(result).hasSize(1),
                () -> assertThat(result.get(0).getTitle()).isEqualTo(category2.getTitle())
        );
    }
}
