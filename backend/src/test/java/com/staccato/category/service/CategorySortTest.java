package com.staccato.category.service;

import com.staccato.category.domain.Category;
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
import com.staccato.fixture.member.MemberFixture;
import com.staccato.fixture.category.CategoryFixture;
import com.staccato.member.domain.Member;
import com.staccato.member.repository.MemberRepository;
import com.staccato.category.repository.CategoryRepository;

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
    void readAllMemoriesByUpdatedAtDesc() {
        // given
        Member member = memberRepository.save(MemberFixture.create());
        List<Category> memories = new ArrayList<>();
        memories.add(categoryRepository.save(CategoryFixture.createWithMember("first", member)));
        memories.add(categoryRepository.save(CategoryFixture.createWithMember("second", member)));

        // when
        List<Category> result = CategorySort.UPDATED.apply(memories);

        // then
        assertAll(
                () -> assertThat(result.get(0).getTitle()).isEqualTo("second"),
                () -> assertThat(result.get(1).getTitle()).isEqualTo("first")
        );
    }

    @DisplayName("최근 생성순으로 카테고리 목록을 조회된다.")
    @Test
    void readAllMemoriesByCreatedAtDesc() {
        // given
        Member member = memberRepository.save(MemberFixture.create());
        List<Category> memories = new ArrayList<>();
        memories.add(categoryRepository.save(CategoryFixture.createWithMember("first", member)));
        memories.add(categoryRepository.save(CategoryFixture.createWithMember("second", member)));

        // when
        List<Category> result = CategorySort.NEWEST.apply(memories);

        // then
        assertAll(
                () -> assertThat(result.get(0).getTitle()).isEqualTo("second"),
                () -> assertThat(result.get(1).getTitle()).isEqualTo("first")
        );
    }

    @DisplayName("오래된 순으로 카테고리 목록을 조회된다.")
    @Test
    void readAllMemoriesByCreatedAtAsc() {
        // given
        Member member = memberRepository.save(MemberFixture.create());
        List<Category> memories = new ArrayList<>();
        memories.add(categoryRepository.save(CategoryFixture.createWithMember("first", member)));
        memories.add(categoryRepository.save(CategoryFixture.createWithMember("second", member)));

        // when
        List<Category> result = CategorySort.OLDEST.apply(memories);

        // then
        assertAll(
                () -> assertThat(result.get(0).getTitle()).isEqualTo("first"),
                () -> assertThat(result.get(1).getTitle()).isEqualTo("second")
        );
    }
}
