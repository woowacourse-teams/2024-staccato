package com.staccato.notification.controller.docs;

import org.springframework.http.ResponseEntity;
import com.staccato.config.auth.LoginMember;
import com.staccato.member.domain.Member;
import com.staccato.notification.service.dto.response.NotificationExistResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

public interface NotificationControllerDocs {
    @Operation(summary = "알림 존재 여부 조회", description = "사용자에 대한 알림이 존재하는지 여부를 조회합니다.")
    @ApiResponse(description = "알림 존재 여부 조회 성공", responseCode = "200")
    ResponseEntity<NotificationExistResponse> isExistNotifications(@Parameter(hidden = true) @LoginMember Member member);
}
