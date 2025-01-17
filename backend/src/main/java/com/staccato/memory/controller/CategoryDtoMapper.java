package com.staccato.memory.controller;

import java.util.List;

import com.staccato.memory.service.dto.request.CategoryRequest;
import com.staccato.memory.service.dto.request.MemoryRequest;
import com.staccato.memory.service.dto.response.CategoryDetailResponse;
import com.staccato.memory.service.dto.response.CategoryIdResponse;
import com.staccato.memory.service.dto.response.CategoryNameResponse;
import com.staccato.memory.service.dto.response.CategoryNameResponses;
import com.staccato.memory.service.dto.response.CategoryResponse;
import com.staccato.memory.service.dto.response.CategoryResponses;
import com.staccato.memory.service.dto.response.MemoryDetailResponse;
import com.staccato.memory.service.dto.response.MemoryIdResponse;
import com.staccato.memory.service.dto.response.MemoryNameResponse;
import com.staccato.memory.service.dto.response.MemoryNameResponses;
import com.staccato.memory.service.dto.response.MemoryResponse;
import com.staccato.memory.service.dto.response.MemoryResponses;
import com.staccato.memory.service.dto.response.MomentResponse;
import com.staccato.memory.service.dto.response.StaccatoResponse;

public class CategoryDtoMapper {
    public static MemoryRequest toMemoryRequest(CategoryRequest categoryRequest) {
        return new MemoryRequest(
                categoryRequest.categoryThumbnailUrl(),
                categoryRequest.categoryTitle(),
                categoryRequest.description(),
                categoryRequest.startAt(),
                categoryRequest.endAt()
        );
    }

    public static CategoryIdResponse toCategoryIdResponse(MemoryIdResponse memoryIdResponse) {
        return new CategoryIdResponse(memoryIdResponse.memoryId());
    }

    public static CategoryResponse toCategoryResponse(MemoryResponse memoryResponse) {
        return new CategoryResponse(
                memoryResponse.memoryId(),
                memoryResponse.memoryThumbnailUrl(),
                memoryResponse.memoryTitle(),
                memoryResponse.startAt(),
                memoryResponse.endAt()
        );
    }

    public static CategoryResponses toCategoryResponses(MemoryResponses memoryResponses) {
        List<CategoryResponse> categoryResponses = memoryResponses.memories().stream()
                .map(CategoryDtoMapper::toCategoryResponse)
                .toList();
        return new CategoryResponses(categoryResponses);
    }

    public static CategoryNameResponse toCategoryNameResponse(MemoryNameResponse memoryNameResponse) {
        return new CategoryNameResponse(memoryNameResponse.memoryId(), memoryNameResponse.memoryTitle());
    }

    public static CategoryNameResponses toCategoryNameResponses(MemoryNameResponses memoryNameResponses) {
        List<CategoryNameResponse> categoryNameResponses = memoryNameResponses.memories().stream()
                .map(CategoryDtoMapper::toCategoryNameResponse)
                .toList();
        return new CategoryNameResponses(categoryNameResponses);
    }

    public static CategoryDetailResponse toCategoryDetailResponse(MemoryDetailResponse memoryDetailResponse) {
        List<StaccatoResponse> staccatoResponses = memoryDetailResponse.moments().stream()
                .map(CategoryDtoMapper::toStaccatoResponse)
                .toList();
        return new CategoryDetailResponse(
                memoryDetailResponse.memoryId(),
                memoryDetailResponse.memoryThumbnailUrl(),
                memoryDetailResponse.memoryTitle(),
                memoryDetailResponse.description(),
                memoryDetailResponse.startAt(),
                memoryDetailResponse.endAt(),
                memoryDetailResponse.mates(),
                staccatoResponses
        );
    }

    private static StaccatoResponse toStaccatoResponse(MomentResponse momentResponse) {
        return new StaccatoResponse(
                momentResponse.momentId(),
                momentResponse.staccatoTitle(),
                momentResponse.momentImageUrl(),
                momentResponse.visitedAt()
        );
    }
}
