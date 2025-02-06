package com.staccato.memory.controller;

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
import com.staccato.config.auth.LoginMember;
import com.staccato.config.log.annotation.Trace;
import com.staccato.member.domain.Member;
import com.staccato.memory.controller.docs.CategoryControllerDocs;
import com.staccato.memory.service.MemoryService;
import com.staccato.memory.service.dto.request.CategoryReadRequest;
import com.staccato.memory.service.dto.request.CategoryRequest;
import com.staccato.memory.service.dto.response.CategoryDetailResponse;
import com.staccato.memory.service.dto.response.CategoryIdResponse;
import com.staccato.memory.service.dto.response.CategoryNameResponses;
import com.staccato.memory.service.dto.response.CategoryResponses;
import com.staccato.memory.service.dto.response.MemoryDetailResponse;
import com.staccato.memory.service.dto.response.MemoryIdResponse;
import com.staccato.memory.service.dto.response.MemoryNameResponses;
import com.staccato.memory.service.dto.response.MemoryResponses;
import lombok.RequiredArgsConstructor;

@Trace
@Validated
@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController implements CategoryControllerDocs {
    private final MemoryService memoryService;

    @PostMapping
    public ResponseEntity<CategoryIdResponse> createCategory(
            @Valid @RequestBody CategoryRequest categoryRequest,
            @LoginMember Member member
    ) {
        MemoryIdResponse memoryIdResponse = memoryService.createMemory(CategoryDtoMapper.toMemoryRequest(categoryRequest), member);
        return ResponseEntity.created(URI.create("/categories/" + memoryIdResponse.memoryId()))
                .body(CategoryDtoMapper.toCategoryIdResponse(memoryIdResponse));
    }

    @GetMapping
    public ResponseEntity<CategoryResponses> readAllCategories(
            @LoginMember Member member,
            @ModelAttribute("CategoryReadRequest") CategoryReadRequest categoryReadRequest
    ) {
        MemoryResponses memoryResponses = memoryService.readAllMemories(member, CategoryDtoMapper.toMemoryReadRequest(categoryReadRequest));
        return ResponseEntity.ok(CategoryDtoMapper.toCategoryResponses(memoryResponses));
    }

    @GetMapping("/candidates")
    public ResponseEntity<CategoryNameResponses> readAllCandidateCategories(
            @LoginMember Member member,
            @RequestParam(value = "currentDate") LocalDate currentDate
    ) {
        MemoryNameResponses memoryNameResponses = memoryService.readAllMemoriesByDate(member, currentDate);
        return ResponseEntity.ok(CategoryDtoMapper.toCategoryNameResponses(memoryNameResponses));
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryDetailResponse> readCategory(
            @LoginMember Member member,
            @PathVariable @Min(value = 1L, message = "카테고리 식별자는 양수로 이루어져야 합니다.") long categoryId) {
        MemoryDetailResponse memoryDetailResponse = memoryService.readMemoryById(categoryId, member);
        return ResponseEntity.ok(CategoryDtoMapper.toCategoryDetailResponse(memoryDetailResponse));
    }

    @PutMapping(path = "/{categoryId}")
    public ResponseEntity<Void> updateCategory(
            @PathVariable @Min(value = 1L, message = "카테고리 식별자는 양수로 이루어져야 합니다.") long categoryId,
            @Valid @RequestBody CategoryRequest categoryRequest,
            @LoginMember Member member) {
        memoryService.updateMemory(CategoryDtoMapper.toMemoryRequest(categoryRequest), categoryId, member);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCategory(
            @PathVariable @Min(value = 1L, message = "카테고리 식별자는 양수로 이루어져야 합니다.") long categoryId,
            @LoginMember Member member) {
        memoryService.deleteMemory(categoryId, member);
        return ResponseEntity.ok().build();
    }
}
