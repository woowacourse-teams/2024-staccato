package com.staccato.category.controller;

import java.net.URI;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.staccato.category.controller.docs.CategoryControllerV3Docs;
import com.staccato.category.service.CategoryService;
import com.staccato.category.service.dto.request.CategoryCreateRequest;
import com.staccato.category.service.dto.response.CategoryIdResponse;
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
}
