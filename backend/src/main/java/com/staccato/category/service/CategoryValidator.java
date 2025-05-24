package com.staccato.category.service;

import org.springframework.stereotype.Component;

import com.staccato.category.domain.Category;
import com.staccato.category.repository.CategoryRepository;
import com.staccato.exception.StaccatoException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CategoryValidator {

    private final CategoryRepository categoryRepository;

    public Category getCategoryByIdOrThrow(long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new StaccatoException("요청하신 카테고리를 찾을 수 없어요."));
    }
}
