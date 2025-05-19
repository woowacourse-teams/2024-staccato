package com.staccato.invitation.controller;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import com.staccato.ControllerTest;
import com.staccato.fixture.member.MemberFixtures;
import com.staccato.invitation.service.dto.request.CategoryInvitationRequest;
import com.staccato.invitation.service.dto.response.InvitationIdResponse;
import com.staccato.member.domain.Member;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class InvitationControllerTest extends ControllerTest {

    @DisplayName("멤버가 닉네임 목록을 통해 카테고리에 초대를 요청한다.")
    @Test
    void inviteMembers() throws Exception {
        // given
        long categoryId = 1L;
        Member member = MemberFixtures.defaultMember().build();
        when(authService.extractFromToken(anyString())).thenReturn(member);
        String invitationRequest = """
                {
                    "categoryId": 1,
                    "inviteeIds": [2, 3]
                }
                """;
        when(invitationService.inviteMembers(any(Member.class), any(CategoryInvitationRequest.class))).thenReturn(new InvitationIdResponse(List.of(1L)));
        String expectedResponse = """
                {
                    "invitationIds" : [1]
                }
                """;

        // when & then
        mockMvc.perform(post("/invitations", categoryId)
                        .header(HttpHeaders.AUTHORIZATION, "token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invitationRequest))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }
}
