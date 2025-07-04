package com.staccato.fixture.category;

import com.staccato.category.domain.Category;
import com.staccato.category.domain.CategoryMember;
import com.staccato.category.domain.Role;
import com.staccato.category.repository.CategoryMemberRepository;
import com.staccato.member.domain.Member;

public class CategoryMemberFixtures {

    public static CategoryMemberBuilder defaultCategoryMember() {
        return new CategoryMemberBuilder()
                .withRole(Role.HOST);
    }

    public static class CategoryMemberBuilder {
        Member member;
        Category category;
        Role role;

        public CategoryMemberBuilder withMember(Member member) {
            this.member = member;
            return this;
        }

        public CategoryMemberBuilder withCategory(Category category) {
            this.category = category;
            return this;
        }

        public CategoryMemberBuilder withRole(Role role) {
            this.role = role;
            return this;
        }

        public CategoryMember build() {
            return new CategoryMember(member, category, role);
        }

        public CategoryMember buildAndSave(CategoryMemberRepository repository) {
            CategoryMember categoryMember = build();
            return repository.save(categoryMember);
        }
    }
}
