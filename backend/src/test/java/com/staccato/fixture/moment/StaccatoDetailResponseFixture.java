package com.staccato.fixture.moment;

import com.staccato.staccato.service.dto.response.StaccatoDetailResponse;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class StaccatoDetailResponseFixture {
    public static StaccatoDetailResponse create(long staccatoId, LocalDateTime visitedAt) {
        return new StaccatoDetailResponse(
                staccatoId,
                1,
                "memoryTitle",
                LocalDate.parse("2024-06-30"),
                LocalDate.parse("2024-07-04"),
                "staccatoTitle",
                List.of("https://example1.com.jpg"),
                visitedAt,
                "happy",
                "placeName",
                "address",
                new BigDecimal("37.77490000000000"),
                new BigDecimal("-122.41940000000000")
        );
    }
}
