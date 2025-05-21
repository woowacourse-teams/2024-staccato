package com.staccato.invitation.controller;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import com.staccato.ControllerTest;
import com.staccato.exception.ExceptionResponse;
import com.staccato.fixture.member.MemberFixtures;
import com.staccato.invitation.service.dto.request.CategoryInvitationRequest;
import com.staccato.invitation.service.dto.response.CategoryInvitationRequestedResponse;
import com.staccato.invitation.service.dto.response.CategoryInvitationRequestedResponses;
import com.staccato.invitation.service.dto.response.InvitationResultResponse;
import com.staccato.invitation.service.dto.response.InvitationResultResponses;
import com.staccato.member.domain.Member;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
        when(invitationService.invite(any(Member.class), any(CategoryInvitationRequest.class)))
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

    @DisplayName("요청한 모든 사용자에 대한 초대가 실패한다면, statusCode는 400을 반환한다.")
    @Test
    void inviteMembersFailure() throws Exception {
        // given
        long categoryId = 1L;
        Member member = MemberFixtures.defaultMember().build();
        when(authService.extractFromToken(anyString())).thenReturn(member);
        CategoryInvitationRequest invitationRequest = new CategoryInvitationRequest(categoryId, Set.of(1L, 2L));
        when(invitationService.invite(any(Member.class), any(CategoryInvitationRequest.class)))
                .thenReturn(new InvitationResultResponses(List.of(
                        new InvitationResultResponse(1L, "400 BAD_REQUEST", "이미 카테고리에 함께하고 있는 사용자입니다.", null),
                        new InvitationResultResponse(2L, "400 BAD_REQUEST", "해당 사용자를 찾을 수 없어요.", null)
                )));

        // when & then
        mockMvc.perform(post("/invitations", categoryId)
                        .header(HttpHeaders.AUTHORIZATION, "token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invitationRequest)))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("유효하지 않은 카테고리 식별자로 초대 요청 시 예외가 발생한다.")
    @Test
    void cannotInviteMembersByInvalidCategoryId() throws Exception {
        // given
        long invalidCategoryId = 0;
        CategoryInvitationRequest request = new CategoryInvitationRequest(invalidCategoryId, Set.of(2L));

        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST.toString(), "카테고리 식별자는 양수로 이루어져야 합니다.");

        // when & then
        mockMvc.perform(post("/invitations")
                        .header(HttpHeaders.AUTHORIZATION, "token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(exceptionResponse)));
    }

    @DisplayName("초대 ID로 초대 요청을 취소한다.")
    @Test
    void cancel() throws Exception {
        // given
        long invitationId = 1L;
        Member member = MemberFixtures.defaultMember().build();
        when(authService.extractFromToken(anyString())).thenReturn(member);

        // when & then
        mockMvc.perform(post("/invitations/{invitationId}/cancel", invitationId)
                        .header(HttpHeaders.AUTHORIZATION, "token"))
                .andExpect(status().isOk());
    }

    @DisplayName("사용자가 잘못된 초대 식별자로 삭제하려고 하면 예외가 발생한다.")
    @Test
    void cannotDeleteCategoryByInvalidId() throws Exception {
        // given
        long invalidId = 0;
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST.toString(), "초대 식별자는 양수로 이루어져야 합니다.");

        // when & then
        mockMvc.perform(post("/invitations/{invitationId}/cancel", invalidId)
                        .header(HttpHeaders.AUTHORIZATION, "token"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(exceptionResponse)));
    }

    @DisplayName("요청자는 자신에게 온 초대 요청 목록을 조회할 수 있다.")
    @Test
    void readRequestedInvitations() throws Exception {
        // given
        Member member = MemberFixtures.defaultMember().build();
        when(authService.extractFromToken(anyString())).thenReturn(member);

        when(invitationService.readInvitations(any(Member.class)))
                .thenReturn(new CategoryInvitationRequestedResponses(List.of(
                        new CategoryInvitationRequestedResponse(2L, "초대한사람", "https://example.com/images/profile1.png", 10L, "여름 방학 여행"),
                        new CategoryInvitationRequestedResponse(3L, "다른사용자", "https://example.com/images/profile2.png", 11L, "겨울 맛집 탐방")
                )));

        String expectedResponse = """
                {
                  "invitations": [
                    {
                      "inviteeId": 2,
                      "inviteeNickname": "초대한사람",
                      "inviteeProfileImageUrl": "https://example.com/images/profile1.png",
                      "categoryId": 10,
                      "categoryTitle": "여름 방학 여행"
                    },
                    {
                      "inviteeId": 3,
                      "inviteeNickname": "다른사용자",
                      "inviteeProfileImageUrl": "https://example.com/images/profile2.png",
                      "categoryId": 11,
                      "categoryTitle": "겨울 맛집 탐방"
                    }
                  ]
                }
                """;

        // when & then
        mockMvc.perform(get("/invitations")
                        .header(HttpHeaders.AUTHORIZATION, "token"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }
}
