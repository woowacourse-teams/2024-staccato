package com.staccato.memory.service.dto.request;

import java.util.ArrayList;
import java.util.List;

public record MemoryReadRequest(String sort, Boolean term) {
    private static final String TERM = "term";
    private static final String ACTIVE = "true";

    public List<String> getFilters() {
        List<String> filters = new ArrayList<>();
        if (isActive(term)) {
            filters.add(TERM.toLowerCase());
        }
        return filters;
    }

    private boolean isActive(String term) {
        return term.equalsIgnoreCase(ACTIVE);
    }
}
