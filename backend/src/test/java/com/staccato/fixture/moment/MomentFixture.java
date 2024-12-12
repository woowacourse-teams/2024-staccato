package com.staccato.fixture.moment;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.staccato.memory.domain.Memory;
import com.staccato.moment.domain.Moment;

public class MomentFixture {
    private static final BigDecimal latitude = new BigDecimal("37.77490000000000");
    private static final BigDecimal longitude = new BigDecimal("-122.41940000000000");

    public static Moment create(Memory memory) {
        return Moment.builder()
                .visitedAt(LocalDateTime.of(2024, 7, 1, 10, 0))
                .title("staccatoTitle")
                .latitude(latitude)
                .longitude(longitude)
                .address("address")
                .placeName("placeName")
                .memory(memory)
                .build();
    }

    public static Moment create(Memory memory, LocalDateTime visitedAt) {
        return Moment.builder()
                .visitedAt(visitedAt)
                .title("staccatoTitle")
                .latitude(latitude)
                .longitude(longitude)
                .placeName("placeName")
                .address("address")
                .memory(memory)
                .build();
    }
}
