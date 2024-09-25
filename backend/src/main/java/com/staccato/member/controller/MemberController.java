package com.staccato.member.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.staccato.config.auth.LoginMember;
import com.staccato.image.service.ImageService;
import com.staccato.image.service.dto.ImageUrlResponse;
import com.staccato.member.controller.docs.MemberControllerDocs;
import com.staccato.member.domain.Member;
import com.staccato.member.service.MemberService;
import com.staccato.member.service.dto.response.MemberProfileResponse;
import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequiredArgsConstructor
public class MemberController implements MemberControllerDocs {
    private final MemberService memberService;
    private final ImageService imageService;

    @PostMapping(path = "/mypage/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MemberProfileResponse> changeProfileImage(
            @RequestPart(value = "imageFile") MultipartFile image,
            @LoginMember Member member
    ) {
        ImageUrlResponse imageUrlResponse = imageService.uploadImage(image);
        MemberProfileResponse memberProfileResponse = memberService.changeProfileImage(member, imageUrlResponse.imageUrl());

        return ResponseEntity.status(HttpStatus.OK).body(memberProfileResponse);
    }
}
