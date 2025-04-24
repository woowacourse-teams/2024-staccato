package com.staccato.fixture.category;

import com.staccato.category.domain.Category;
import java.time.LocalDate;
import com.staccato.member.domain.Member;

public class CategoryFixture {
    public static Category create() {
        return Category.builder()
                .thumbnailUrl("https://example.com/categories/geumohrm.jpg")
                .title("title")
                .description("친구들과 함께한 여름 휴가 추억")
                .build();
    }

    public static Category create(String title) {
        return Category.builder()
                .thumbnailUrl("https://example.com/categories/geumohrm.jpg")
                .title(title)
                .description("친구들과 함께한 여름 휴가 추억")
                .build();
    }

    public static Category create(LocalDate startAt, LocalDate endAt) {
        return Category.builder()
                .thumbnailUrl("https://example.com/categories/geumohrm.jpg")
                .title("2024 여름 휴가")
                .description("친구들과 함께한 여름 휴가 추억")
                .startAt(startAt)
                .endAt(endAt)
                .build();
    }

    public static Category create(String title, LocalDate startAt, LocalDate endAt) {
        return Category.builder()
            .thumbnailUrl("https://example.com/categories/geumohrm.jpg")
            .title(title)
            .description("친구들과 함께한 여름 휴가 추억")
            .startAt(startAt)
            .endAt(endAt)
            .build();
    }

    public static Category createWithMember(Member member) {
        Category category = Category.builder()
                .thumbnailUrl("https://example.com/categories/geumohrm.jpg")
                .title("2024 여름 휴가")
                .description("친구들과 함께한 여름 휴가 추억")
                .startAt(LocalDate.of(2024, 7, 1))
                .endAt(LocalDate.of(2024, 7, 10))
                .build();
        category.addCategoryMember(member);

        return category;
    }

    public static Category createWithMember(String title, Member member) {
        Category category = Category.builder()
                .thumbnailUrl("https://example.com/categories/geumohrm.jpg")
                .title(title)
                .description("친구들과 함께한 여름 휴가 추억")
                .build();
        category.addCategoryMember(member);

        return category;
    }

    public static Category createWithMember(LocalDate startAt, LocalDate endAt, Member member) {
        Category category = Category.builder()
                .thumbnailUrl("https://example.com/categories/geumohrm.jpg")
                .title("2024 여름 휴가")
                .description("친구들과 함께한 여름 휴가 추억")
                .startAt(startAt)
                .endAt(endAt)
                .build();
        category.addCategoryMember(member);

        return category;
    }
}
