package com.staccato.fixture.moment;

import java.math.BigDecimal;
import java.util.List;

import com.staccato.moment.service.dto.response.MomentResponse;
import com.staccato.moment.service.dto.response.MomentResponses;

public class MomentResponsesFixture {
    public static MomentResponses create() {
        return new MomentResponses(
                List.of(new MomentResponse(1, BigDecimal.ONE, BigDecimal.ZERO),
                        new MomentResponse(2, BigDecimal.ONE, BigDecimal.ZERO),
                        new MomentResponse(3, BigDecimal.ONE, BigDecimal.ZERO)));
    }
}
