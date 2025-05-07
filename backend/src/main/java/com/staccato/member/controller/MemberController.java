package com.staccato.member.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.staccato.member.service.MemberService;
import com.staccato.member.service.dto.response.MemberResponses;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @GetMapping("/search")
    public ResponseEntity<MemberResponses> readMembersByNickname(@RequestParam(value = "nickname") String nickname) {
        MemberResponses memberResponses = memberService.readMembersByNickname(nickname);
        return ResponseEntity.ok(memberResponses);
    }
}
