package com.staccato.member.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.staccato.config.auth.LoginMember;
import com.staccato.image.service.ImageService;
import com.staccato.image.service.dto.ImageUrlResponse;
import com.staccato.member.controller.docs.MyPageControllerDocs;
import com.staccato.member.domain.Member;
import com.staccato.member.service.MemberService;
import com.staccato.member.service.dto.response.MemberProfileImageResponse;
import com.staccato.member.service.dto.response.MemberProfileResponse;
import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequiredArgsConstructor
public class MyPageController implements MyPageControllerDocs {
    private final MemberService memberService;
    private final ImageService imageService;

    @PostMapping(path = "/mypage/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MemberProfileImageResponse> changeProfileImage(
            @RequestPart(value = "imageFile") MultipartFile image,
            @LoginMember Member member
    ) {
        ImageUrlResponse imageUrlResponse = imageService.uploadImage(image);
        MemberProfileImageResponse memberProfileImageResponse = memberService.changeProfileImage(member, imageUrlResponse.imageUrl());

        return ResponseEntity.ok(memberProfileImageResponse);
    }

    @GetMapping("/mypage")
    public ResponseEntity<MemberProfileResponse> readMyPage(
            @LoginMember Member member
    ) {
        MemberProfileResponse memberProfileResponse = new MemberProfileResponse(member);

        return ResponseEntity.ok(memberProfileResponse);
    }
}
