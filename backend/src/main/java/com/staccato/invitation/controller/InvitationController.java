package com.staccato.invitation.controller;

import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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
import com.staccato.invitation.service.dto.response.InvitationResultResponses;
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
    public ResponseEntity<InvitationResultResponses> inviteMembers(@LoginMember Member member,
                                                                   @RequestBody CategoryInvitationRequest categoryInvitationRequest) {
        InvitationResultResponses invitationResultResponses = invitationService.invite(member, categoryInvitationRequest);
        return ResponseEntity.ok(invitationResultResponses);
    }

    @PostMapping("/{invitationId}/cancel")
    public ResponseEntity<Void> cancelInvitation(
            @LoginMember Member member,
            @Min(value = 1L, message = "초대 식별자는 양수로 이루어져야 합니다.") @PathVariable long invitationId
    ) {
        invitationService.cancel(member, invitationId);
        return ResponseEntity.ok().build();
    }
}
