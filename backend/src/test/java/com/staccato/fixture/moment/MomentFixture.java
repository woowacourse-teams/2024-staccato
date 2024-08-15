package com.staccato.fixture.moment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.staccato.memory.domain.Memory;
import com.staccato.moment.domain.Moment;

public class MomentFixture {
    private static final BigDecimal latitude = new BigDecimal("37.7749");
    private static final BigDecimal longitude = new BigDecimal("-122.4194");

    public static Moment create(Memory memory, LocalDateTime visitedAt) {
        return Moment.builder()
                .visitedAt(visitedAt)
                .placeName("placeName")
                .latitude(latitude)
                .longitude(longitude)
                .address("address")
                .memory(memory)
                .build();
    }
}
