package com.staccato.notification.controller;

import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.staccato.config.auth.LoginMember;
import com.staccato.member.domain.Member;
import com.staccato.notification.controller.docs.NotificationControllerDocs;
import com.staccato.notification.service.NotificationService;
import com.staccato.notification.service.dto.response.NotificationExistResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@Validated
public class NotificationController implements NotificationControllerDocs {

    private final NotificationService notificationService;

    @GetMapping("/exists")
    public ResponseEntity<NotificationExistResponse> isExistNotifications(@LoginMember Member member) {
        NotificationExistResponse response = notificationService.isExistNotifications(member);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/token")
    public ResponseEntity<Void> register(
            @LoginMember Member member, @RequestParam(value = "token") @NotBlank(message = "토큰 값은 필수이며, 공백일 수 없습니다.") String token
    ) {
        notificationService.register(member, token);
        return ResponseEntity.ok().build();
    }
}
