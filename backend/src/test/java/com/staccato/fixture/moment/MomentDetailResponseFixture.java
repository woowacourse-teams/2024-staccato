package com.staccato.fixture.moment;

import java.time.LocalDate;
import java.util.List;

import com.staccato.moment.service.dto.response.MomentDetailResponse;

public class MomentDetailResponseFixture {
    public static MomentDetailResponse create(long momentId, LocalDate visitedAt) {
        return new MomentDetailResponse(momentId,
                "placeName",
                List.of("https://example1.com.jpg"),
                visitedAt,
                "happy",
                "address",
                List.of());
    }
}
