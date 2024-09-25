package com.staccato.fixture.moment;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.staccato.moment.service.dto.response.MomentDetailResponse;

public class MomentDetailResponseFixture {
    public static MomentDetailResponse create(long momentId, LocalDateTime visitedAt) {
        return new MomentDetailResponse(
                momentId,
                1,
                "memoryTitle",
                "staccatoTitle",
                List.of("https://example1.com.jpg"),
                visitedAt,
                "happy",
                "placeName",
                "address",
                new BigDecimal("37.77490000000000"),
                new BigDecimal("-122.41940000000000"),
                List.of());
    }
}
