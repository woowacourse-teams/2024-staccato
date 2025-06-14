package com.staccato.notification.service.dto.request;

import jakarta.validation.constraints.NotBlank;
import com.staccato.config.swagger.SwaggerExamples;
import com.staccato.notification.domain.DeviceType;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "알람 토큰 등록")
public record NotificationTokenRequest(
        @Schema(example = SwaggerExamples.NOTIFICATION_TOKEN)
        @NotBlank(message = "토큰 값을 입력해주세요.")
        String token,
        @Schema(example = SwaggerExamples.DEVICE_TYPE)
        @NotBlank(message = "디바이스 타입을 입력해주세요.")
        String deviceType,
        @Schema(example = SwaggerExamples.DEVICE_ID)
        @NotBlank(message = "디바이스 식별값을 입력해주세요.")
        String deviceId) {

    public DeviceType toDeviceType() {
        return DeviceType.findByName(deviceType);
    }
}
