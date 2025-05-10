package com.staccato.category.controller;

import java.net.URI;
import java.time.LocalDate;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.staccato.category.controller.docs.CategoryControllerDocs;
import com.staccato.category.service.CategoryService;
import com.staccato.category.service.dto.request.CategoryColorRequest;
import com.staccato.category.service.dto.request.CategoryInvitationRequest;
import com.staccato.category.service.dto.request.CategoryReadRequest;
import com.staccato.category.service.dto.request.CategoryRequest;
import com.staccato.category.service.dto.request.CategoryStaccatoLocationRangeRequest;
import com.staccato.category.service.dto.response.CategoryDetailResponse;
import com.staccato.category.service.dto.response.CategoryDetailResponseV2;
import com.staccato.category.service.dto.response.CategoryIdResponse;
import com.staccato.category.service.dto.response.CategoryNameResponses;
import com.staccato.category.service.dto.response.CategoryResponses;
import com.staccato.category.service.dto.response.CategoryResponsesV2;
import com.staccato.category.service.dto.response.CategoryStaccatoLocationResponses;
import com.staccato.config.auth.LoginMember;
import com.staccato.config.log.annotation.Trace;
import com.staccato.member.domain.Member;

import lombok.RequiredArgsConstructor;

@Trace
@Validated
@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController implements CategoryControllerDocs {
    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryIdResponse> createCategory(
            @Valid @RequestBody CategoryRequest categoryRequest,
            @LoginMember Member member
    ) {
        CategoryIdResponse categoryIdResponse = categoryService.createCategory(categoryRequest.toCategoryCreateRequest(), member);
        return ResponseEntity.created(URI.create("/categories/" + categoryIdResponse.categoryId()))
                .body(categoryIdResponse);
    }

    @GetMapping
    public ResponseEntity<CategoryResponses> readAllCategories(
            @LoginMember Member member,
            @ModelAttribute("CategoryReadRequest") CategoryReadRequest categoryReadRequest
    ) {
        CategoryResponsesV2 categoryResponses = categoryService.readAllCategories(member, categoryReadRequest);
        return ResponseEntity.ok(categoryResponses.toCategoryResponses());
    }

    @GetMapping("/candidates")
    public ResponseEntity<CategoryNameResponses> readAllCandidateCategories(
            @LoginMember Member member,
            @RequestParam(value = "currentDate") LocalDate currentDate
    ) {
        CategoryNameResponses categoryNameResponses = categoryService.readAllCategoriesByDate(member, currentDate);
        return ResponseEntity.ok(categoryNameResponses);
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryDetailResponse> readCategory(
            @LoginMember Member member,
            @PathVariable @Min(value = 1L, message = "카테고리 식별자는 양수로 이루어져야 합니다.") long categoryId) {
        CategoryDetailResponseV2 categoryDetailResponse = categoryService.readCategoryById(categoryId, member);
        return ResponseEntity.ok(categoryDetailResponse.toCategoryDetailResponse());
    }

    @GetMapping("/{categoryId}/staccatos")
    public ResponseEntity<CategoryStaccatoLocationResponses> readAllStaccatoByCategory(
            @LoginMember Member member,
            @PathVariable @Min(value = 1L, message = "카테고리 식별자는 양수로 이루어져야 합니다.") long categoryId,
            @Validated @ModelAttribute CategoryStaccatoLocationRangeRequest categoryStaccatoLocationRangeRequest
    ) {
        CategoryStaccatoLocationResponses categoryStaccatoLocationResponses = categoryService.readAllStaccatoByCategory(
                member, categoryId, categoryStaccatoLocationRangeRequest);
        return ResponseEntity.ok().body(categoryStaccatoLocationResponses);
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<Void> updateCategory(
            @PathVariable @Min(value = 1L, message = "카테고리 식별자는 양수로 이루어져야 합니다.") long categoryId,
            @Valid @RequestBody CategoryRequest categoryRequest,
            @LoginMember Member member) {
        categoryService.updateCategory(categoryRequest.toCategoryUpdateRequest(), categoryId, member);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{categoryId}/colors")
    public ResponseEntity<Void> updateCategoryColor(
            @PathVariable @Min(value = 1L, message = "카테고리 식별자는 양수로 이루어져야 합니다.") long categoryId,
            @Valid @RequestBody CategoryColorRequest categoryColorRequest,
            @LoginMember Member member) {
        categoryService.updateCategoryColor(categoryId, categoryColorRequest, member);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCategory(
            @PathVariable @Min(value = 1L, message = "카테고리 식별자는 양수로 이루어져야 합니다.") long categoryId,
            @LoginMember Member member) {
        categoryService.deleteCategory(categoryId, member);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{categoryId}/invitations")
    public ResponseEntity<Void> invitation(@PathVariable @Min(value = 1L, message = "카테고리 식별자는 양수로 이루어져야 합니다.") long categoryId,
                                           @LoginMember Member member,
                                           @RequestBody CategoryInvitationRequest categoryInvitationRequest) {
        categoryService.invitation(categoryId, member, categoryInvitationRequest);
        return ResponseEntity.ok().build();
    }
}
