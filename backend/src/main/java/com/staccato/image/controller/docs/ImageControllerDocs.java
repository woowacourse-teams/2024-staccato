package com.staccato.image.controller.docs;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.staccato.config.auth.LoginMember;
import com.staccato.image.service.dto.ImageUrlResponse;
import com.staccato.image.service.dto.ImageUrlResponses;
import com.staccato.member.domain.Member;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Image", description = "Image API")
public interface ImageControllerDocs {

    @Operation(summary = "이미지 업로드", description = "이미지를 업로드하고 S3 url을 가져옵니다.")
    @ApiResponses(value = {
            @ApiResponse(description = "이미지 업로드 성공", responseCode = "201"),
            @ApiResponse(description = "전송된 파일이 손상되었거나 지원되지 않는 형식일 때", responseCode = "400"),
            @ApiResponse(description = "20MB 초과의 사진을 업로드 하려고 할 때", responseCode = "413")
    })
    ResponseEntity<ImageUrlResponse> uploadImage(
            @Parameter(description = "업로드할 이미지 파일 (PNG, JPG, JPEG, WEBP) 형식 지원, 최대 20MB",
                    required = true,
                    content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
            @RequestPart(value = "imageFile") MultipartFile image,
            @Parameter(hidden = true) @LoginMember Member member);

    @Operation(summary = "이미지 업로드", description = "이미지를 업로드하고 S3 url을 가져옵니다.")
    @ApiResponses(value = {
            @ApiResponse(description = "이미지 업로드 성공", responseCode = "201"),
            @ApiResponse(description = "전송된 파일이 손상되었거나 지원되지 않는 형식일 때", responseCode = "400"),
            @ApiResponse(description = "20MB 초과의 사진을 업로드 하려고 할 때", responseCode = "413")
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImageUrlResponses> uploadImages(
            @Parameter(description = "업로드할 이미지 파일 목록 (PNG, JPG, JPEG, WEBP) 형식 지원, 최대 20MB",
                    required = true,
                    content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
            @RequestPart(value = "imageFiles") List<MultipartFile> images,
            @Parameter(hidden = true) @LoginMember Member member
    );
}
