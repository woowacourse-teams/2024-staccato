package com.staccato.member.controller;

import jakarta.validation.constraints.Min;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController implements MemberControllerDocs {
    private final MemberService memberService;
    private final ImageService imageService;

    @PostMapping(path = "/{memberId}/profiles/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MemberProfileResponse> changeProfileImage(
            @RequestPart(value = "imageFile") MultipartFile image,
            @LoginMember Member member,
            @PathVariable("memberId") @Min(value = 1L, message = "사용자 식별자는 양수로 이루어져야 합니다.") long memberId
    ) {
        ImageUrlResponse imageUrlResponse = imageService.uploadImage(image);
        MemberProfileResponse memberProfileResponse = memberService.changeProfileImage(member, memberId, imageUrlResponse.imageUrl());

        return ResponseEntity.status(HttpStatus.CREATED).body(memberProfileResponse);
    }
}
