package com.staccato.category.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import com.staccato.ServiceSliceTest;
import com.staccato.category.domain.Category;
import com.staccato.category.repository.CategoryRepository;
import com.staccato.fixture.category.CategoryFixtures;
import com.staccato.fixture.member.MemberFixtures;
import com.staccato.member.domain.Member;
import com.staccato.member.repository.MemberRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class CategorySortTest extends ServiceSliceTest {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    @DisplayName("정렬명이 주어졌을 때 대소문자 구분 없이 CategorySort을 반환한다.")
    @ParameterizedTest
    @CsvSource({
            "UPDATED, UPDATED",
            "NEWEST, NEWEST",
            "OLDEST, OLDEST",
            "updated, UPDATED",
            "newest, NEWEST",
            "oldest, OLDEST"
    })
    void findByNameWithValidSort(String name, CategorySort sort) {
        // when
        CategorySort result = CategorySort.findByName(name);

        // then
        assertThat(result).isEqualTo(sort);
    }

    @DisplayName("유효하지 않거나 null인 정렬명이 주어졌을 때 기본값인 UPDATED를 반환한다.")
    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"invalid"})
    void findByNameWithInvalidOrNull(String name) {
        // when
        CategorySort result = CategorySort.findByName(name);

        // then
        assertThat(result).isEqualTo(CategorySort.UPDATED);
    }

    @DisplayName("수정 시간 기준 내림차순으로 카테고리 목록을 조회된다.")
    @Test
    void readAllCategoriesByUpdatedAtDesc() {
        // given
        Member member = MemberFixtures.defaultMember().buildAndSave(memberRepository);
        List<Category> categories = new ArrayList<>();
        categories.add(CategoryFixtures.defaultCategory()
                .withTitle("first")
                .withHost(member)
                .buildAndSave(categoryRepository));
        categories.add(CategoryFixtures.defaultCategory()
                .withTitle("second")
                .withHost(member)
                .buildAndSave(categoryRepository));

        // when
        List<Category> result = CategorySort.UPDATED.apply(categories);

        // then
        assertAll(
                () -> assertThat(result.get(0).getTitle()).isEqualTo("second"),
                () -> assertThat(result.get(1).getTitle()).isEqualTo("first")
        );
    }

    @DisplayName("최근순으로 카테고리 목록을 조회된다. - 기간 있는 건 시작 날짜 기준, 그 외 최근수정순")
    @Test
    void readAllCategoriesByNewest() {
        // given
        List<Category> categories = new ArrayList<>();
        categories.add(CategoryFixtures.defaultCategory()
                .withTitle("first")
                .withTerm(LocalDate.of(2024, 1, 1),
                        LocalDate.of(2024, 12, 31))
                .buildAndSave(categoryRepository));
        categories.add(CategoryFixtures.defaultCategory()
                .withTitle("second")
                .withTerm(LocalDate.of(2024, 6, 1),
                        LocalDate.of(2024, 12, 31))
                .buildAndSave(categoryRepository));
        categories.add(CategoryFixtures.defaultCategory()
                .withTitle("third")
                .withTerm(null, null)
                .buildAndSave(categoryRepository));
        categories.add(CategoryFixtures.defaultCategory()
                .withTitle("fourth")
                .withTerm(null, null)
                .buildAndSave(categoryRepository));

        // when
        List<Category> result = CategorySort.NEWEST.apply(categories);

        // then
        assertAll(
                () -> assertThat(result.get(0).getTitle()).isEqualTo("second"),
                () -> assertThat(result.get(1).getTitle()).isEqualTo("first"),
                () -> assertThat(result.get(2).getTitle()).isEqualTo("fourth"),
                () -> assertThat(result.get(3).getTitle()).isEqualTo("third")
        );
    }

    @DisplayName("오래된 순으로 카테고리 목록을 조회된다. - 기간 있는 건 시작 날짜 기준, 그 외 최근수정순")
    @Test
    void readAllCategoriesByOldest() {
        // given
        List<Category> categories = new ArrayList<>();
        categories.add(CategoryFixtures.defaultCategory()
                .withTitle("first")
                .withTerm(LocalDate.of(2024, 1, 1),
                        LocalDate.of(2024, 12, 31))
                .buildAndSave(categoryRepository));
        categories.add(CategoryFixtures.defaultCategory()
                .withTitle("second")
                .withTerm(LocalDate.of(2024, 6, 1),
                        LocalDate.of(2024, 12, 31))
                .buildAndSave(categoryRepository));
        categories.add(CategoryFixtures.defaultCategory()
                .withTitle("third")
                .withTerm(null, null)
                .buildAndSave(categoryRepository));
        categories.add(CategoryFixtures.defaultCategory()
                .withTitle("fourth")
                .withTerm(null, null)
                .buildAndSave(categoryRepository));

        // when
        List<Category> result = CategorySort.OLDEST.apply(categories);

        // then
        assertAll(
                () -> assertThat(result.get(0).getTitle()).isEqualTo("first"),
                () -> assertThat(result.get(1).getTitle()).isEqualTo("second"),
                () -> assertThat(result.get(2).getTitle()).isEqualTo("fourth"),
                () -> assertThat(result.get(3).getTitle()).isEqualTo("third")
        );
    }
}
