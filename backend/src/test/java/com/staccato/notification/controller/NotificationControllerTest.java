package com.staccato.notification.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import com.staccato.ControllerTest;
import com.staccato.exception.ExceptionResponse;
import com.staccato.fixture.member.MemberFixtures;
import com.staccato.member.domain.Member;
import com.staccato.notification.service.NotificationService;
import com.staccato.notification.service.dto.response.NotificationExistResponse;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class NotificationControllerTest extends ControllerTest {
    @Autowired
    private NotificationService notificationService;

    @DisplayName("사용자에 대한 알림이 존재하는지 조회한다.")
    @DisplayName("알림용 토큰을 등록합니다.")
    @Test
    void readMyPage() throws Exception {
    void register() throws Exception {
        // given
        Member member = MemberFixtures.defaultMember().build();
        when(authService.extractFromToken(anyString())).thenReturn(member);
        when(notificationService.isExistNotifications(any(Member.class))).thenReturn(new NotificationExistResponse(true));
        String expectedResponse = """
                {
                	"isExist": true
                }
                """;
        when(authService.extractFromToken(anyString())).thenReturn(MemberFixtures.defaultMember().build());

        // when & then
        mockMvc.perform(get("/notifications/exists")
        mockMvc.perform(post("/notification/token")
                        .param("token", "FCM_Token")
                        .header(HttpHeaders.AUTHORIZATION, "token"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
                .andExpect(status().isOk());
    }

    @DisplayName("알림용 토큰이 공백으로 전달되는 경우 등록에 실패합니다.")
    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    void failRegister(String token) throws Exception {
        // given
        when(authService.extractFromToken(anyString())).thenReturn(MemberFixtures.defaultMember().build());
        ExceptionResponse exceptionResponse = new ExceptionResponse(
                HttpStatus.BAD_REQUEST.toString(), "토큰 값은 필수이며, 공백일 수 없습니다.");

        // when & then
        mockMvc.perform(post("/notification/token")
                        .param("token", token)
                        .header(HttpHeaders.AUTHORIZATION, "token"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(exceptionResponse)));
    }
}
