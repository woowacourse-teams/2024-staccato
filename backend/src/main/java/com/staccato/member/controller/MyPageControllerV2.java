package com.staccato.member.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.staccato.config.auth.LoginMember;
import com.staccato.member.controller.docs.MyPageControllerV2Docs;
import com.staccato.member.domain.Member;
import com.staccato.member.service.dto.response.MemberProfileResponseV2;
import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequiredArgsConstructor
public class MyPageControllerV2 implements MyPageControllerV2Docs {
    @GetMapping("/v2/mypage")
    public ResponseEntity<MemberProfileResponseV2> readMyPage(
            @LoginMember Member member
    ) {
        MemberProfileResponseV2 memberProfileResponse = new MemberProfileResponseV2(member);

        return ResponseEntity.ok(memberProfileResponse);
    }
}
