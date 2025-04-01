package com.staccato.fixture.category;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.staccato.category.domain.Category;
import com.staccato.category.domain.CategoryMember;
import com.staccato.category.domain.Term;
import com.staccato.category.repository.CategoryRepository;
import com.staccato.member.domain.Member;

public class CategoryFixtures {

    public static CategoryBuilder defaultCategory() {
        return new CategoryBuilder()
                .withThumbnailUrl("https://example.com/categoryThumbnail.jpg")
                .withTitle("categoryTitle")
                .withDescription("categoryDescription")
                .withTerm(LocalDate.of(2024, 1, 1),
                        LocalDate.of(2024, 12, 31));
    }

    public static class CategoryBuilder {
        private Long id;
        private String thumbnailUrl;
        private String title;
        private String description;
        private Term term;
        private final List<CategoryMember> members = new ArrayList<>();

        public CategoryBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public CategoryBuilder withThumbnailUrl(String thumbnailUrl) {
            this.thumbnailUrl = thumbnailUrl;
            return this;
        }

        public CategoryBuilder withTitle(String title) {
            this.title = title;
            return this;
        }

        public CategoryBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public CategoryBuilder withTerm(LocalDate startAt, LocalDate endAt) {
            this.term = new Term(startAt, endAt);
            return this;
        }

        public Category build() {
            return new Category(id, thumbnailUrl, title, description, term, members);
        }

        public Category buildWithMember(Member member) {
            Category category = build();
            category.addCategoryMember(member);
            return category;
        }

        public Category buildAndSave(CategoryRepository repository) {
            Category category = build();
            return repository.save(category);
        }

        public Category buildAndSaveWithMember(Member member, CategoryRepository repository) {
            Category category = buildWithMember(member);
            return repository.save(category);
        }
    }
}
