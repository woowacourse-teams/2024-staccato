package com.staccato.invitation.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import com.staccato.invitation.service.dto.response.CategoryInvitationCreateResponses;
import com.staccato.invitation.service.dto.response.CategoryInvitationReceivedResponse;
import com.staccato.invitation.service.dto.response.CategoryInvitationReceivedResponses;
import com.staccato.invitation.service.dto.response.CategoryInvitationSentResponse;
import com.staccato.invitation.service.dto.response.CategoryInvitationSentResponses;
import com.staccato.member.domain.Member;

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
                .thenReturn(new CategoryInvitationCreateResponses(List.of(1L, 2L)));
        String expectedResponse = """
                {
                  "invitationIds": [1, 2]
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

    @DisplayName("사용자가 잘못된 초대 식별자로 취소하려고 하면 예외가 발생한다.")
    @Test
    void cannotCancelInvitationByInvalidId() throws Exception {
        // given
        long invalidId = 0;
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST.toString(), "초대 식별자는 양수로 이루어져야 합니다.");

        // when & then
        mockMvc.perform(post("/invitations/{invitationId}/cancel", invalidId)
                        .header(HttpHeaders.AUTHORIZATION, "token"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(exceptionResponse)));
    }

    @DisplayName("요청자는 자신이 보낸 초대 요청 목록을 조회할 수 있다.")
    @Test
    void readSentInvitations() throws Exception {
        // given
        Member member = MemberFixtures.defaultMember().build();
        when(authService.extractFromToken(anyString())).thenReturn(member);

        when(invitationService.readSentInvitations(any(Member.class)))
                .thenReturn(new CategoryInvitationSentResponses(List.of(
                        new CategoryInvitationSentResponse(1L, 2L, "초대한사람", "https://example.com/images/profile1.png", 10L, "여름 방학 여행"),
                        new CategoryInvitationSentResponse(2L, 3L, "다른사용자", "https://example.com/images/profile2.png", 11L, "겨울 맛집 탐방")
                )));

        String expectedResponse = """
                {
                  "invitations": [
                    {
                      "invitationId": 1,
                      "inviteeId": 2,
                      "inviteeNickname": "초대한사람",
                      "inviteeProfileImageUrl": "https://example.com/images/profile1.png",
                      "categoryId": 10,
                      "categoryTitle": "여름 방학 여행"
                    },
                    {
                      "invitationId": 2,
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
        mockMvc.perform(get("/invitations/sent")
                        .header(HttpHeaders.AUTHORIZATION, "token"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @DisplayName("초대 ID로 초대 요청을 수락한다.")
    @Test
    void accept() throws Exception {
        // given
        long invitationId = 1L;
        Member member = MemberFixtures.defaultMember().build();
        when(authService.extractFromToken(anyString())).thenReturn(member);

        // when & then
        mockMvc.perform(post("/invitations/{invitationId}/accept", invitationId)
                        .header(HttpHeaders.AUTHORIZATION, "token"))
                .andExpect(status().isOk());
    }

    @DisplayName("사용자가 잘못된 초대 식별자로 수락하려고 하면 예외가 발생한다.")
    @Test
    void cannotAcceptInvitationByInvalidId() throws Exception {
        // given
        long invalidId = 0;
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST.toString(), "초대 식별자는 양수로 이루어져야 합니다.");

        // when & then
        mockMvc.perform(post("/invitations/{invitationId}/cancel", invalidId)
                        .header(HttpHeaders.AUTHORIZATION, "token"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(exceptionResponse)));
    }

    @DisplayName("초대 ID로 초대 요청을 거절한다.")
    @Test
    void reject() throws Exception {
        // given
        long invitationId = 1L;
        Member member = MemberFixtures.defaultMember().build();
        when(authService.extractFromToken(anyString())).thenReturn(member);

        // when & then
        mockMvc.perform(post("/invitations/{invitationId}/reject", invitationId)
                        .header(HttpHeaders.AUTHORIZATION, "token"))
                .andExpect(status().isOk());
    }

    @DisplayName("사용자가 잘못된 초대 식별자로 거절하려고 하면 예외가 발생한다.")
    @Test
    void cannotRejectInvitationByInvalidId() throws Exception {
        // given
        long invalidId = 0;
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST.toString(), "초대 식별자는 양수로 이루어져야 합니다.");

        // when & then
        mockMvc.perform(post("/invitations/{invitationId}/reject", invalidId)
                        .header(HttpHeaders.AUTHORIZATION, "token"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(exceptionResponse)));
    }

    @DisplayName("요청자는 자신이 받은 초대 요청 목록을 조회할 수 있다.")
    @Test
    void readReceivedInvitations() throws Exception {
        // given
        Member member = MemberFixtures.defaultMember().build();
        when(authService.extractFromToken(anyString())).thenReturn(member);

        when(invitationService.readReceivedInvitations(any(Member.class)))
                .thenReturn(new CategoryInvitationReceivedResponses(List.of(
                        new CategoryInvitationReceivedResponse(1L, 2L, "초대보낸사람1", "https://example.com/images/profile1.png", 10L, "여름 방학 여행"),
                        new CategoryInvitationReceivedResponse(2L, 3L, "초대보낸사람2", "https://example.com/images/profile2.png", 11L, "겨울 맛집 탐방")
                )));

        String expectedResponse = """
                {
                  "invitations": [
                    {
                      "invitationId": 1,
                      "inviterId": 2,
                      "inviterNickname": "초대보낸사람1",
                      "inviterProfileImageUrl": "https://example.com/images/profile1.png",
                      "categoryId": 10,
                      "categoryTitle": "여름 방학 여행"
                    },
                    {
                      "invitationId": 2,
                      "inviterId": 3,
                      "inviterNickname": "초대보낸사람2",
                      "inviterProfileImageUrl": "https://example.com/images/profile2.png",
                      "categoryId": 11,
                      "categoryTitle": "겨울 맛집 탐방"
                    }
                  ]
                }
                """;

        // when & then
        mockMvc.perform(get("/invitations/received")
                        .header(HttpHeaders.AUTHORIZATION, "token"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }
}
