package com.staccato.member.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import com.staccato.ControllerTest;
import com.staccato.fixture.member.MemberFixtures;
import com.staccato.member.domain.Member;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MyPageControllerV2Test extends ControllerTest {
    @DisplayName("마이페이지의 사용자 정보를 조회한다.")
    @Test
    void readMyPage() throws Exception {
        // given
        Member member = MemberFixtures.defaultMember()
                .withNickname("nickname")
                .withCode("550e8400-e29b-41d4-a716-446655440000").build();
        when(authService.extractFromToken(anyString())).thenReturn(member);
        String expectedResponse = """
                {
                    "memberId": null,
                	"nickname": "nickname",
                    "profileImageUrl": "https://example.com/memberImage.png",
                    "code": "550e8400-e29b-41d4-a716-446655440000"
                }
                """;

        // when & then
        mockMvc.perform(get("/v2/mypage")
                        .header(HttpHeaders.AUTHORIZATION, "token"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse, true));
    }
}
