package com.staccato.invitation.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.staccato.config.auth.LoginMember;
import com.staccato.config.log.annotation.Trace;
import com.staccato.invitation.controller.docs.InvitationControllerDocs;
import com.staccato.invitation.service.InvitationService;
import com.staccato.invitation.service.dto.request.CategoryInvitationRequest;
import com.staccato.invitation.service.dto.response.CategoryInvitationCreateResponses;
import com.staccato.invitation.service.dto.response.CategoryInvitationRequestedResponses;
import com.staccato.member.domain.Member;
import lombok.RequiredArgsConstructor;

@Trace
@Validated
@RestController
@RequestMapping("/invitations")
@RequiredArgsConstructor
public class InvitationController implements InvitationControllerDocs {
    private final InvitationService invitationService;

    @PostMapping
    public ResponseEntity<CategoryInvitationCreateResponses> inviteMembers(@LoginMember Member member,
                                                                           @Valid @RequestBody CategoryInvitationRequest categoryInvitationRequest) {
        CategoryInvitationCreateResponses categoryInvitationCreateResponses = invitationService.invite(member, categoryInvitationRequest);
        return ResponseEntity.ok(categoryInvitationCreateResponses);
    }

    @PostMapping("/{invitationId}/cancel")
    public ResponseEntity<Void> cancelInvitation(
            @LoginMember Member member,
            @Min(value = 1L, message = "초대 식별자는 양수로 이루어져야 합니다.") @PathVariable long invitationId
    ) {
        invitationService.cancel(member, invitationId);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<CategoryInvitationRequestedResponses> readRequestedInvitations(@LoginMember Member member) {
        CategoryInvitationRequestedResponses responses = invitationService.readInvitations(member);

        return ResponseEntity.ok(responses);
    }

    @PostMapping("/{invitationId}/accept")
    public ResponseEntity<Void> acceptInvitation(
            @LoginMember Member member,
            @Min(value = 1L, message = "초대 식별자는 양수로 이루어져야 합니다.") @PathVariable long invitationId
    ) {
        invitationService.accept(member, invitationId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{invitationId}/reject")
    public ResponseEntity<Void> rejectInvitation(
            @LoginMember Member member,
            @Min(value = 1L, message = "초대 식별자는 양수로 이루어져야 합니다.") @PathVariable long invitationId
    ) {
        invitationService.reject(member, invitationId);
        return ResponseEntity.ok().build();
    }
}
