package com.staccato.moment.service.dto.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;

import com.staccato.memory.domain.Memory;
import com.staccato.moment.domain.Moment;
import com.staccato.moment.domain.MomentImages;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "순간 생성 시 요청 형식입니다. 단, 멀티파트로 보내는 사진 파일은 여기에 포함되지 않습니다.")
public record MomentRequest(
        @Schema(example = "런던 박물관")
        @NotBlank(message = "순간 장소의 이름을 입력해주세요.")
        @Size(min = 1, max = 30, message = "순간 장소의 이름은 공백 포함 1자 이상 30자 이하로 설정해주세요.")
        String placeName,
        @Schema(example = "Great Russell St, London WC1B 3DG")
        @NotNull(message = "순간 장소의 주소를 입력해주세요.")
        String address,
        @Schema(example = "51.51978412729915")
        @NotNull(message = "순간 장소의 위도를 입력해주세요.")
        BigDecimal latitude,
        @Schema(example = "-0.12712788587027796")
        @NotNull(message = "순간 장소의 경도를 입력해주세요.")
        BigDecimal longitude,
        @Schema(example = "2024-07-27")
        @NotNull(message = "방문 날짜를 입력해주세요.")
        @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime visitedAt,
        @Schema(example = "1")
        @NotNull(message = "추억을 선택해주세요.")
        @Min(value = 1L, message = "추억 식별자는 양수로 이루어져야 합니다.")
        long memoryId,
        @ArraySchema(
                arraySchema = @Schema(example = "[\"https://example.com/images/namsan_tower.jpg\", \"https://example.com/images/namsan_tower2.jpg\"]"))
        @Size(max = 5, message = "사진은 5장까지만 추가할 수 있어요.")
        List<String> momentImageUrls
) {
    public Moment toMoment(Memory memory) {
        return Moment.builder()
                .visitedAt(visitedAt)
                .placeName(placeName)
                .latitude(latitude)
                .longitude(longitude)
                .address(address)
                .memory(memory)
                .momentImages(new MomentImages(momentImageUrls))
                .build();
    }
}
