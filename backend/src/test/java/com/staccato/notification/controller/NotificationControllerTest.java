package com.staccato.notification.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import com.staccato.ControllerTest;
import com.staccato.fixture.member.MemberFixtures;
import com.staccato.member.domain.Member;
import com.staccato.notification.service.NotificationService;
import com.staccato.notification.service.dto.response.NotificationExistResponse;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class NotificationControllerTest extends ControllerTest {
    @Autowired
    private NotificationService notificationService;

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
}
