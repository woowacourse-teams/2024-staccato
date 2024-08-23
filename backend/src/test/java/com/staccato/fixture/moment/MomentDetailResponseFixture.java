package com.staccato.fixture.moment;

import java.time.LocalDateTime;
import java.util.List;

import com.staccato.moment.service.dto.response.MomentDetailResponse;

public class MomentDetailResponseFixture {
    public static MomentDetailResponse create(long momentId, LocalDateTime visitedAt) {
        return new MomentDetailResponse(
                momentId,
                1,
                "memoryTitle",
                "placeName",
                List.of("https://example1.com.jpg"),
                visitedAt,
                "happy",
                "address",
                List.of());
    }
}
