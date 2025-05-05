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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.staccato.category.controller.docs.CategoryControllerV2Docs;
import com.staccato.category.service.CategoryService;
import com.staccato.category.service.dto.request.CategoryReadRequest;
import com.staccato.category.service.dto.request.CategoryRequestV2;
import com.staccato.category.service.dto.response.CategoryDetailResponseV2;
import com.staccato.category.service.dto.response.CategoryIdResponse;
import com.staccato.category.service.dto.response.CategoryResponsesV2;
import com.staccato.config.auth.LoginMember;
import com.staccato.config.log.annotation.Trace;
import com.staccato.member.domain.Member;
import lombok.RequiredArgsConstructor;

@Trace
@Validated
@RestController
@RequestMapping("/v2/categories")
@RequiredArgsConstructor
public class CategoryControllerV2 implements CategoryControllerV2Docs {
    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryIdResponse> createCategory(
            @Valid @RequestBody CategoryRequestV2 categoryRequest,
            @LoginMember Member member
    ) {
        CategoryIdResponse categoryIdResponse = categoryService.createCategory(categoryRequest.toCategoryRequestV3(), member);
        return ResponseEntity.created(URI.create("/categories/" + categoryIdResponse.categoryId()))
                .body(categoryIdResponse);
    }

    @GetMapping
    public ResponseEntity<CategoryResponsesV2> readAllCategories(
            @LoginMember Member member,
            @ModelAttribute("CategoryReadRequest") CategoryReadRequest categoryReadRequest
    ) {
        CategoryResponsesV2 categoryResponses = categoryService.readAllCategories(member, categoryReadRequest);
        return ResponseEntity.ok(categoryResponses);
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryDetailResponseV2> readCategory(
            @LoginMember Member member,
            @PathVariable @Min(value = 1L, message = "카테고리 식별자는 양수로 이루어져야 합니다.") long categoryId) {
        CategoryDetailResponseV2 categoryDetailResponse = categoryService.readCategoryById(categoryId, member);
        return ResponseEntity.ok(categoryDetailResponse);
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<Void> updateCategory(
            @PathVariable @Min(value = 1L, message = "카테고리 식별자는 양수로 이루어져야 합니다.") long categoryId,
            @Valid @RequestBody CategoryRequestV2 categoryRequest,
            @LoginMember Member member) {
        categoryService.updateCategory(categoryRequest, categoryId, member);
        return ResponseEntity.ok().build();
    }
}
