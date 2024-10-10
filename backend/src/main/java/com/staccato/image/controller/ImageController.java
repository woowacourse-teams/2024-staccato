package com.staccato.image.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.staccato.config.auth.LoginMember;
import com.staccato.config.log.annotation.Trace;
import com.staccato.image.controller.docs.ImageControllerDocs;
import com.staccato.image.service.ImageService;
import com.staccato.image.service.dto.ImageUrlResponse;
import com.staccato.member.domain.Member;

import lombok.RequiredArgsConstructor;

@Trace
@RestController
@RequestMapping("/images")
@RequiredArgsConstructor
public class ImageController implements ImageControllerDocs {
    private final ImageService imageService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImageUrlResponse> uploadImage(
            @RequestPart(value = "imageFile") MultipartFile image,
            @LoginMember Member member
    ) {
        ImageUrlResponse imageUrlResponse = imageService.uploadImage(image);

        return ResponseEntity.status(HttpStatus.CREATED).body(imageUrlResponse);
    }
}
