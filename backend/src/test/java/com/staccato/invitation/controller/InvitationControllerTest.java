package com.staccato.invitation.controller;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import com.staccato.ControllerTest;
import com.staccato.fixture.member.MemberFixtures;
import com.staccato.invitation.service.dto.request.CategoryInvitationRequest;
import com.staccato.invitation.service.dto.response.InvitationResultResponse;
import com.staccato.invitation.service.dto.response.InvitationResultResponses;
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
        when(invitationService.inviteMembers(any(Member.class), any(CategoryInvitationRequest.class)))
                .thenReturn(new InvitationResultResponses(List.of(
                        new InvitationResultResponse(1L, "200 OK", "초대 요청에 성공하였습니다.", 1L),
                        new InvitationResultResponse(2L, "400 BAD_REQUEST", "이미 카테고리에 함께하고 있는 사용자입니다.", null),
                        new InvitationResultResponse(3L, "400 BAD_REQUEST", "해당 사용자를 찾을 수 없어요.", null)
                )));
        String expectedResponse = """
                {
                  "invitationResults": [
                    {
                      "inviteeId": 1,
                      "statusCode": "200 OK",
                      "message": "초대 요청에 성공하였습니다.",
                      "invitationId": 1
                    },
                    {
                      "inviteeId": 2,
                      "statusCode": "400 BAD_REQUEST",
                      "message": "이미 카테고리에 함께하고 있는 사용자입니다."
                    },
                    {
                      "inviteeId": 3,
                      "statusCode": "400 BAD_REQUEST",
                      "message": "해당 사용자를 찾을 수 없어요."
                    }
                  ]
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
