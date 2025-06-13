package com.staccato.notification.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import com.staccato.ControllerTest;
import com.staccato.exception.ExceptionResponse;
import com.staccato.fixture.member.MemberFixtures;
import com.staccato.member.domain.Member;
import com.staccato.notification.service.dto.response.NotificationExistResponse;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class NotificationControllerTest extends ControllerTest {

    @DisplayName("사용자에 대한 알림이 존재하는지 조회한다.")
    @Test
    void readMyPage() throws Exception {
        // given
        Member member = MemberFixtures.defaultMember().build();
        when(authService.extractFromToken(anyString())).thenReturn(member);
        when(notificationService.isExistNotifications(any(Member.class))).thenReturn(new NotificationExistResponse(true));
        String expectedResponse = """
                {
                	"isExist": true
                }
                """;

        // when & then
        mockMvc.perform(get("/notifications/exists")
                        .header(HttpHeaders.AUTHORIZATION, "token"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @DisplayName("알림용 토큰을 등록합니다.")
    @Test
    void register() throws Exception {
        // given
        when(authService.extractFromToken(anyString())).thenReturn(MemberFixtures.defaultMember().build());
        String notificationTokenRequest = """
                {
                  "token": "example-fcm-token",
                  "deviceType": "ANDROID",
                  "deviceId": "device-123456"
                }
                """;
        // when & then
        mockMvc.perform(post("/notifications/tokens")
                        .content(notificationTokenRequest)
                        .header(HttpHeaders.AUTHORIZATION, "token"))
                .andExpect(status().isOk());
    }

    @DisplayName("알림용 토큰이 공백으로 전달되는 경우 등록에 실패합니다.")
    @Test
    void failRegister() throws Exception {
        // given
        when(authService.extractFromToken(anyString())).thenReturn(MemberFixtures.defaultMember().build());
        ExceptionResponse exceptionResponse = new ExceptionResponse(
                HttpStatus.BAD_REQUEST.toString(), "토큰 값을 입력해주세요.");
        String notificationTokenRequest = """
                {
                  "token": " ",
                  "deviceType": "ANDROID",
                  "deviceId": "device-123456"
                }
                """;
        // when & then
        mockMvc.perform(post("/notifications/tokens")
                        .content(notificationTokenRequest)
                        .header(HttpHeaders.AUTHORIZATION, "token"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(exceptionResponse)));
    }
}
