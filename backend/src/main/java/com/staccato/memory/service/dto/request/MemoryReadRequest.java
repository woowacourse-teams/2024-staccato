package com.staccato.memory.service.dto.request;

import java.util.Objects;

public record MemoryReadRequest(String sort, Boolean term) {
    private static final String DEFAULT_SORT = "UPDATED";
    private static final boolean DEFAULT_FLAG = false;

    public MemoryReadRequest(String sort, Boolean term) {
        this.sort = Objects.nonNull(sort) ? sort : DEFAULT_SORT;
        this.term = Objects.nonNull(term) ? term : DEFAULT_FLAG;
    }
}
