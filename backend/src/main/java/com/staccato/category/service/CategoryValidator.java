package com.staccato.category.service;

import org.springframework.stereotype.Component;

import com.staccato.category.domain.Category;
import com.staccato.category.repository.CategoryMemberRepository;
import com.staccato.category.repository.CategoryRepository;
import com.staccato.exception.ForbiddenException;
import com.staccato.exception.StaccatoException;
import com.staccato.member.domain.Member;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CategoryValidator {

    private final CategoryRepository categoryRepository;
    private final CategoryMemberRepository categoryMemberRepository;

    public Category getCategoryByIdOrThrow(long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new StaccatoException("요청하신 카테고리를 찾을 수 없어요."));
    }

    public void validateCategoryTitleAlreadyExists(Category category, Member member) {
        if (categoryMemberRepository.existsByMemberAndCategoryTitle(member, category.getTitle())) {
            throw new StaccatoException("같은 이름을 가진 카테고리가 있어요. 다른 이름으로 설정해주세요.");
        }
    }

    public void validateCategoryTitleAlreadyExists(Category origin, Category updated, Member member) {
        if (origin.isNotSameTitle(updated.getTitle()) &&
                categoryMemberRepository.existsByMemberAndCategoryTitle(member, updated.getTitle())) {
            throw new StaccatoException("같은 이름을 가진 카테고리가 있어요. 다른 이름으로 설정해주세요.");
        }
    }

    public void validateReadPermission(Category category, Member member) {
        if (category.isUnreadableBy(member)) {
            throw new ForbiddenException();
        }
    }

    public void validateModifyPermission(Category category, Member member) {
        if (category.isUnmodifiableBy(member)) {
            throw new ForbiddenException();
        }
    }
}
