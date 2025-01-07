package com.staccato.memory.service.dto.request;

import java.util.Objects;

public record MemoryReadRequest(String sort, Boolean term) {
    public MemoryReadRequest(String sort, Boolean term) {
        this.sort = sort;
        this.term = Objects.nonNull(term) ? term : false;
    }
}
