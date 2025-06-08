package com.staccato.notification.controller.docs;

import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import com.staccato.member.domain.Member;
import com.staccato.notification.service.dto.response.NotificationExistResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Notification", description = "Notification API")
public interface NotificationControllerDocs {

    @Operation(summary = "알림 존재 여부 조회", description = "사용자에 대한 알림이 존재하는지 여부를 조회합니다.")
    @ApiResponse(description = "알림 존재 여부 조회 성공", responseCode = "200")
    ResponseEntity<NotificationExistResponse> isExistNotifications(@Parameter(hidden = true) Member member);

    @Operation(summary = "토큰 등록", description = "FCM 토큰을 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(description = "토큰 등록 성공", responseCode = "200"),
            @ApiResponse(description = """
                    <발생 가능한 케이스>
                                        
                    (1) token 값이 누락되거나 공백일 때

                    """,
                    responseCode = "400")
    })
    ResponseEntity<Void> register(
            @Parameter(hidden = true) Member member,
            @Parameter(description = "FCM token 값", example = "token") @NotBlank(message = "토큰 값은 필수이며, 공백일 수 없습니다.") String token
    );
}
