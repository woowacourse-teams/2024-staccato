package com.staccato.category.controller;

import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.staccato.category.controller.docs.CategoryControllerV4Docs;
import com.staccato.category.service.CategoryService;
import com.staccato.category.service.dto.response.CategoryDetailResponseV4;
import com.staccato.config.auth.LoginMember;
import com.staccato.config.log.annotation.Trace;
import com.staccato.member.domain.Member;
import lombok.RequiredArgsConstructor;

@Trace
@Validated
@RestController
@RequestMapping("/v4/categories")
@RequiredArgsConstructor
public class CategoryControllerV4 implements CategoryControllerV4Docs {
    private final CategoryService categoryService;

    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryDetailResponseV4> readCategory(
            @LoginMember Member member,
            @PathVariable @Min(value = 1L, message = "카테고리 식별자는 양수로 이루어져야 합니다.") long categoryId) {
        CategoryDetailResponseV4 categoryDetailResponse = categoryService.readCategoryById(categoryId, member);
        return ResponseEntity.ok(categoryDetailResponse);
    }
}
