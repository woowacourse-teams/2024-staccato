package com.staccato.fixture.category;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import com.staccato.category.domain.Category;
import com.staccato.category.domain.Color;
import com.staccato.category.domain.Term;
import com.staccato.category.repository.CategoryRepository;
import com.staccato.member.domain.Member;

public class CategoryFixtures {

    public static CategoryBuilder defaultCategory() {
        return new CategoryBuilder()
                .withThumbnailUrl("https://example.com/categoryThumbnail.jpg")
                .withTitle("categoryTitle")
                .withDescription("categoryDescription")
                .withColor(Color.PINK)
                .withTerm(LocalDate.of(2024, 1, 1),
                        LocalDate.of(2024, 12, 31))
                .withIsShared(false);
    }

    public static class CategoryBuilder {
        private String thumbnailUrl;
        private String title;
        private String description;
        private Color color;
        private Term term;
        private Boolean isShared;
        private Member host;
        private List<Member> guests = new ArrayList<>();

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

        public CategoryBuilder withColor(Color color) {
            this.color = color;
            return this;
        }

        public CategoryBuilder withIsShared(Boolean isShared) {
            this.isShared = isShared;
            return this;
        }

        public CategoryBuilder withHost(Member member) {
            this.host = member;
            return this;
        }

        public CategoryBuilder withGuests(List<Member> members) {
            this.guests.addAll(members);
            this.isShared = true;
            return this;
        }

        public Category build() {
            Category category = new Category(thumbnailUrl, title, description, color, term.getStartAt(), term.getEndAt(), isShared);
            if (Objects.nonNull(host)) {
                category.addHost(host);
            }
            category.addGuests(guests);
            return category;
        }

        public Category buildAndSave(CategoryRepository repository) {
            Category category = build();
            return repository.save(category);
        }
    }
}
