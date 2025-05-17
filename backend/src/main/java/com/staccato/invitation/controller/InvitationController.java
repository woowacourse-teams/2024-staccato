package com.staccato.invitation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.staccato.invitation.service.dto.CategoryInvitationRequest;
import com.staccato.config.auth.LoginMember;
import com.staccato.config.log.annotation.Trace;
import com.staccato.invitation.controller.docs.InvitationControllerDocs;
import com.staccato.invitation.service.InvitationService;
import com.staccato.member.domain.Member;
import lombok.RequiredArgsConstructor;

@Trace
@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class InvitationController implements InvitationControllerDocs {
    private final InvitationService invitationService;

    @PostMapping("/{categoryId}/members}")
    public ResponseEntity<Void> inviteMembers(@PathVariable  long categoryId,
                                              @LoginMember Member member,
                                              @RequestBody CategoryInvitationRequest categoryInvitationRequest) {
        invitationService.inviteMembers(categoryId, member, categoryInvitationRequest);
        return ResponseEntity.ok().build();
    }
}
