package com.staccato.member.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.staccato.config.auth.LoginMember;
import com.staccato.member.domain.Member;
import com.staccato.member.service.MemberService;
import com.staccato.member.service.dto.request.MemberReadRequest;
import com.staccato.member.service.dto.response.MemberResponses;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController implements MemberControllerDocs {
    private final MemberService memberService;

    @GetMapping("/search")
    public ResponseEntity<MemberResponses> readMembersByNickname(
            @LoginMember Member member, @ModelAttribute MemberReadRequest memberReadRequest
    ) {
        MemberResponses memberResponses = memberService.readMembersByNickname(member, memberReadRequest);
        return ResponseEntity.ok(memberResponses);
    }
}
