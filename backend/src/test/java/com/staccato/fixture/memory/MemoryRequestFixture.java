package com.staccato.fixture.memory;

import java.time.LocalDate;

import com.staccato.memory.service.dto.request.MemoryRequest;

public class MemoryRequestFixture {
    public static MemoryRequest create(LocalDate startAt, LocalDate endAt) {
        return new MemoryRequest(
                "https://example.com/memorys/geumohrm.jpg",
                "2023 여름 휴가",
                "친구들과 함께한 여름 휴가 추억",
                startAt,
                endAt
        );
    }

    public static MemoryRequest create(LocalDate startAt, LocalDate endAt, String title) {
        return new MemoryRequest(
                "https://example.com/memorys/geumohrm.jpg",
                title,
                "친구들과 함께한 여름 휴가 추억",
                startAt,
                endAt
        );
    }

    public static MemoryRequest create(String imageUrl, LocalDate startAt, LocalDate endAt) {
        return new MemoryRequest(
                imageUrl,
                "2023 여름 휴가",
                "친구들과 함께한 여름 휴가 추억",
                startAt,
                endAt
        );
    }
}
