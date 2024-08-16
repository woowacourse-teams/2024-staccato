package com.staccato.fixture.memory;

import java.time.LocalDate;

import com.staccato.memory.domain.Memory;

public class MemoryFixture {
    public static Memory create() {
        return Memory.builder()
                .thumbnailUrl("https://example.com/memorys/geumohrm.jpg")
                .title("2024 여름 휴가")
                .description("친구들과 함께한 여름 휴가 추억")
                .startAt(LocalDate.of(2024, 7, 1))
                .endAt(LocalDate.of(2024, 7, 10))
                .build();
    }

    public static Memory create(LocalDate startAt, LocalDate endAt) {
        return Memory.builder()
                .thumbnailUrl("https://example.com/memorys/geumohrm.jpg")
                .title("2024 여름 휴가")
                .description("친구들과 함께한 여름 휴가 추억")
                .startAt(startAt)
                .endAt(endAt)
                .build();
    }
}