package com.staccato.staccato.service.dto.request;

import com.staccato.config.swagger.SwaggerExamples;
import com.staccato.staccato.domain.Staccato;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;

import com.staccato.category.domain.Category;
import com.staccato.staccato.domain.StaccatoImages;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "스타카토 생성 시 요청 형식입니다. 단, 멀티파트로 보내는 사진 파일은 여기에 포함되지 않습니다.")
public record StaccatoRequest(
        @Schema(example = SwaggerExamples.STACCATO_TITLE)
        @NotBlank(message = "스타카토 제목을 입력해주세요.")
        @Size(max = 30, message = "스타카토 제목은 공백 포함 30자 이하로 설정해주세요.")
        String staccatoTitle,
        @Schema(example = SwaggerExamples.STACCATO_PLACE_NAME)
        @NotNull(message = "장소 이름을 입력해주세요.")
        String placeName,
        @Schema(example = SwaggerExamples.STACCATO_ADDRESS)
        @NotNull(message = "스타카토의 주소를 입력해주세요.")
        String address,
        @Schema(example = SwaggerExamples.STACCATO_LATITUDE)
        @NotNull(message = "스타카토의 위도를 입력해주세요.")
        BigDecimal latitude,
        @Schema(example = SwaggerExamples.STACCATO_LONGITUDE)
        @NotNull(message = "스타카토의 경도를 입력해주세요.")
        BigDecimal longitude,
        @Schema(example = SwaggerExamples.STACCATO_VISITED_AT)
        @NotNull(message = "스타카토 날짜를 입력해주세요.")
        @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime visitedAt,
        @Schema(example = SwaggerExamples.CATEGORY_ID)
        @NotNull(message = "카테고리를 선택해주세요.")
        @Min(value = 1L, message = "카테고리 식별자는 양수로 이루어져야 합니다.")
        long categoryId,
        @ArraySchema(
                arraySchema = @Schema(example = SwaggerExamples.IMAGE_URLS))
        @Size(max = 5, message = "사진은 5장까지만 추가할 수 있어요.")
        List<String> staccatoImageUrls
) {
    public StaccatoRequest {
        if (Objects.nonNull(staccatoTitle)) {
            staccatoTitle = staccatoTitle.trim();
        }
    }

    public Staccato toStaccato(Category category) {
        return Staccato.builder()
                .visitedAt(visitedAt)
                .title(staccatoTitle)
                .placeName(placeName)
                .latitude(latitude)
                .longitude(longitude)
                .address(address)
                .category(category)
                .staccatoImages(new StaccatoImages(staccatoImageUrls))
                .build();
    }
}
