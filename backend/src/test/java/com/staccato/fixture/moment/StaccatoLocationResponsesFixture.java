package com.staccato.fixture.moment;

import com.staccato.staccato.service.dto.response.StaccatoLocationResponse;
import com.staccato.staccato.service.dto.response.StaccatoLocationResponses;
import java.math.BigDecimal;
import java.util.List;

public class StaccatoLocationResponsesFixture {
    public static StaccatoLocationResponses create() {
        return new StaccatoLocationResponses(
                List.of(new StaccatoLocationResponse(1, BigDecimal.ONE, BigDecimal.ZERO),
                        new StaccatoLocationResponse(2, BigDecimal.ONE, BigDecimal.ZERO),
                        new StaccatoLocationResponse(3, BigDecimal.ONE, BigDecimal.ZERO)));
    }
}
