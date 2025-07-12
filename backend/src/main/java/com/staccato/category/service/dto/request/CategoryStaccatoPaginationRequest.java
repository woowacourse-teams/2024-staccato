package com.staccato.category.service.dto.request;

public record CategoryStaccatoPaginationRequest(
        String cursor,
        int limit
) {
}
