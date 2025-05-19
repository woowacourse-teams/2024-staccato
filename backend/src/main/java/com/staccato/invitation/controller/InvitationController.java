package com.staccato.invitation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.staccato.config.auth.LoginMember;
import com.staccato.config.log.annotation.Trace;
import com.staccato.invitation.controller.docs.InvitationControllerDocs;
import com.staccato.invitation.service.InvitationService;
import com.staccato.invitation.service.dto.request.CategoryInvitationRequest;
import com.staccato.invitation.service.dto.response.InvitationIdResponse;
import com.staccato.member.domain.Member;
import lombok.RequiredArgsConstructor;

@Trace
@RestController
@RequestMapping("/invitations")
@RequiredArgsConstructor
public class InvitationController implements InvitationControllerDocs {
    private final InvitationService invitationService;

    @PostMapping
    public ResponseEntity<InvitationIdResponse> inviteMembers(@LoginMember Member member,
                                              @RequestBody CategoryInvitationRequest categoryInvitationRequest) {
        InvitationIdResponse invitationIdResponse = invitationService.inviteMembers(member, categoryInvitationRequest);
        return ResponseEntity.ok(invitationIdResponse);
    }
}
