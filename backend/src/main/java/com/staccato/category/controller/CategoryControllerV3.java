package com.staccato.category.controller;

import java.net.URI;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.staccato.category.controller.docs.CategoryControllerV3Docs;
import com.staccato.category.service.CategoryService;
import com.staccato.category.service.dto.request.CategoryCreateRequest;
import com.staccato.category.service.dto.request.CategoryReadRequest;
import com.staccato.category.service.dto.response.CategoryDetailResponseV3;
import com.staccato.category.service.dto.response.CategoryIdResponse;
import com.staccato.category.service.dto.response.CategoryResponsesV3;
import com.staccato.config.auth.LoginMember;
import com.staccato.config.log.annotation.Trace;
import com.staccato.member.domain.Member;
import lombok.RequiredArgsConstructor;

@Trace
@Validated
@RestController
@RequestMapping("/v3/categories")
@RequiredArgsConstructor
public class CategoryControllerV3 implements CategoryControllerV3Docs {
    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryIdResponse> createCategory(
            @Valid @RequestBody CategoryCreateRequest categoryCreateRequest,
            @LoginMember Member member
    ) {
        CategoryIdResponse categoryIdResponse = categoryService.createCategory(categoryCreateRequest, member);
        return ResponseEntity.created(URI.create("/categories/" + categoryIdResponse.categoryId()))
                .body(categoryIdResponse);
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryDetailResponseV3> readCategory(
            @LoginMember Member member,
            @PathVariable @Min(value = 1L, message = "카테고리 식별자는 양수로 이루어져야 합니다.") long categoryId) {
        CategoryDetailResponseV3 categoryDetailResponse = categoryService.readCategoryById(categoryId, member);
        return ResponseEntity.ok(categoryDetailResponse);
    }

    @GetMapping
    public ResponseEntity<CategoryResponsesV3> readAllCategories(
            @LoginMember Member member,
            @ModelAttribute("CategoryReadRequest") CategoryReadRequest categoryReadRequest
    ) {
        CategoryResponsesV3 categoryResponses = categoryService.readAllCategories(member, categoryReadRequest);
        return ResponseEntity.ok(categoryResponses);
    }
}
