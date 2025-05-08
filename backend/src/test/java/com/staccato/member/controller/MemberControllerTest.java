package com.staccato.member.controller;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import com.staccato.ControllerTest;
import com.staccato.fixture.member.MemberFixtures;
import com.staccato.member.domain.Member;
import com.staccato.member.service.dto.response.MemberResponses;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MemberControllerTest extends ControllerTest {

    @DisplayName("닉네임으로 검색 요청/응답의 역직렬화/직렬화에 성공한다.")
    @Test
    void readMembersByNickname() throws Exception {
        // given
        Member member = MemberFixtures.defaultMember().withNickname("스타카토").build();
        Member member2 = MemberFixtures.defaultMember().withNickname("스타").build();
        MemberResponses memberResponses = MemberResponses.of(List.of(member2));
        when(authService.extractFromToken(anyString())).thenReturn(member);
        when(memberService.readMembersByNickname(any(Member.class), anyString())).thenReturn(memberResponses);

        String expectedResponse = """
                {
                    "members": [
                        {
                            "memberId": null,
                            "nickname": "스타",
                            "memberImageUrl": "https://example.com/memberImage.png"
                        }
                    ]
                }
                """;

        // when & then
        mockMvc.perform(get("/members/search")
                        .param("nickname", "스타")
                        .header(HttpHeaders.AUTHORIZATION, "token"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

}
