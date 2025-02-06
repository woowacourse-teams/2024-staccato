package com.staccato.fixture.moment;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import com.staccato.moment.service.dto.request.MomentRequest;

public class MomentRequestFixture {

    public static MomentRequest create(Long memoryId) {
        return new MomentRequest(
                "staccatoTitle",
                "placeName",
                "address",
                BigDecimal.ONE,
                BigDecimal.ONE,
                LocalDateTime.now(),
                memoryId,
                List.of());
    }

    public static MomentRequest create(long memoryId, List<String> momentImages) {
        return new MomentRequest(
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
