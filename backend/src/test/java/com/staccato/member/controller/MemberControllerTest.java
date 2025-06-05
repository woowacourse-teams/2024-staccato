package com.staccato.member.controller;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import com.staccato.ControllerTest;
import com.staccato.fixture.member.MemberFixtures;
import com.staccato.member.domain.Member;
import com.staccato.member.service.dto.request.MemberReadRequest;
import com.staccato.member.service.dto.response.MemberSearchResponses;

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
        MemberSearchResponses memberSearchResponses = MemberSearchResponses.none(List.of(member2));
        when(authService.extractFromToken(anyString())).thenReturn(member);
        when(memberService.readMembersByNickname(any(Member.class), any(MemberReadRequest.class))).thenReturn(memberSearchResponses);

        String expectedResponse = """
                {
                    "members": [
                        {
                            "memberId": null,
                            "nickname": "스타",
                            "memberImageUrl": "https://example.com/memberImage.png",
                            "status": "NONE"
                        }
                    ]
                }
                """;

        // when & then
        mockMvc.perform(get("/members/search")
                        .param("nickname", "스타")
                        .param("excludeCategoryId", "0")
                        .header(HttpHeaders.AUTHORIZATION, "token"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @DisplayName("요청 인자로 검색어가 없어도 예외가 발생하지 않는다.")
    @Test
    void readMemberWithoutNickname() throws Exception {
        // given
        Member member = MemberFixtures.defaultMember().withNickname("스타카토").build();
        MemberSearchResponses response = MemberSearchResponses.empty();
        when(authService.extractFromToken(anyString())).thenReturn(member);
        when(memberService.readMembersByNickname(any(Member.class), any(MemberReadRequest.class))).thenReturn(response);

        String expectedResponse = """
                {
                    "members": []
                }
                """;

        // when & then
        mockMvc.perform(get("/members/search")
                        .param("excludeCategoryId", "1")
                        .header(HttpHeaders.AUTHORIZATION, "token"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @DisplayName("요청 인자로 카테고리 식별자가 없어도 예외가 발생하지 않는다.")
    @Test
    void readMemberWithoutCategoryId() throws Exception {
        // given
        Member member = MemberFixtures.defaultMember().withNickname("스타카토").build();
        Member member2 = MemberFixtures.defaultMember().withNickname("스타").build();
        MemberSearchResponses response = MemberSearchResponses.none(List.of(member2));
        when(authService.extractFromToken(anyString())).thenReturn(member);
        when(memberService.readMembersByNickname(any(Member.class), any(MemberReadRequest.class))).thenReturn(response);

        String expectedResponse = """
                {
                    "members": [
                        {
                            "memberId": null,
                            "nickname": "스타",
                            "memberImageUrl": "https://example.com/memberImage.png",
                            "status": "NONE"
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
