package com.staccato.fixture.moment;

import com.staccato.staccato.service.dto.request.StaccatoRequest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class StaccatoRequestFixture {

    public static StaccatoRequest create(Long categoryId) {
        return new StaccatoRequest(
                "staccatoTitle",
                "placeName",
                "address",
                BigDecimal.ONE,
                BigDecimal.ONE,
                LocalDateTime.now(),
                categoryId,
                List.of());
    }

    public static StaccatoRequest create(long memoryId, List<String> momentImages) {
        return new StaccatoRequest(
                "staccatoTitle",
                "placeName",
                "address",
                BigDecimal.ONE,
                BigDecimal.ONE,
                LocalDateTime.now(),
                memoryId,
                momentImages);
    }
}
