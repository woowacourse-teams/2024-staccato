package com.staccato.notification.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.staccato.config.auth.LoginMember;
import com.staccato.member.domain.Member;
import com.staccato.notification.controller.docs.NotificationControllerDocs;
import com.staccato.notification.service.NotificationService;
import com.staccato.notification.service.dto.request.NotificationTokenRequest;
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
            @LoginMember Member member, @RequestBody @Valid NotificationTokenRequest notificationTokenRequest
    ) {
        notificationService.register(member, notificationTokenRequest);
        return ResponseEntity.ok().build();
    }
}
