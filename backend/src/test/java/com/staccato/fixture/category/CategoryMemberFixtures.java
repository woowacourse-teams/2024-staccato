package com.staccato.fixture.category;

import com.staccato.category.domain.Category;
import com.staccato.category.domain.CategoryMember;
import com.staccato.category.repository.CategoryMemberRepository;
import com.staccato.member.domain.Member;

public class CategoryMemberFixtures {

    public static CategoryMemberBuilder defaultCategoryMember() {
        return new CategoryMemberBuilder();
    }

    public static class CategoryMemberBuilder {
        Long id;
        Member member;
        Category category;

        public CategoryMemberBuilder withMember(Member member) {
            this.member = member;
            return this;
        }

        public CategoryMemberBuilder withCategory(Category category) {
            this.category = category;
            return this;
        }

        public CategoryMember build() {
            return new CategoryMember(id, member, category);
        }

        public CategoryMember buildAndSave(CategoryMemberRepository repository) {
            CategoryMember categoryMember = build();
            return repository.save(categoryMember);
        }
    }
}
