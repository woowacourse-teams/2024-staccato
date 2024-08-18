package com.staccato.fixture.moment;

import java.math.BigDecimal;
import java.util.List;

import com.staccato.moment.service.dto.response.MomentLocationResponse;
import com.staccato.moment.service.dto.response.MomentLocationResponses;

public class MomentResponsesFixture {
    public static MomentLocationResponses create() {
        return new MomentLocationResponses(
                List.of(new MomentLocationResponse(1, BigDecimal.ONE, BigDecimal.ZERO),
                        new MomentLocationResponse(2, BigDecimal.ONE, BigDecimal.ZERO),
                        new MomentLocationResponse(3, BigDecimal.ONE, BigDecimal.ZERO)));
    }
}
