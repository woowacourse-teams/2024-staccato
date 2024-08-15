package com.staccato.moment.controller;

import java.net.URI;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.staccato.config.auth.LoginMember;
import com.staccato.member.domain.Member;
import com.staccato.moment.controller.docs.MomentControllerDocs;
import com.staccato.moment.service.MomentService;
import com.staccato.moment.service.dto.request.MomentRequest;
import com.staccato.moment.service.dto.request.MomentUpdateRequest;
import com.staccato.moment.service.dto.response.MomentDetailResponse;
import com.staccato.moment.service.dto.response.MomentIdResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/moments")
@RequiredArgsConstructor
@Validated
public class MomentController implements MomentControllerDocs {
    private final MomentService momentService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MomentIdResponse> createMoment(
            @LoginMember Member member,
            @Valid @RequestPart(value = "data") MomentRequest momentRequest,
            @Size(max = 5, message = "사진은 5장까지만 추가할 수 있어요.") @RequestPart(value = "momentImageFiles") List<MultipartFile> momentImageFiles
    ) {
        MomentIdResponse momentIdResponse = momentService.createMoment(momentRequest, momentImageFiles, member);
        return ResponseEntity.created(URI.create("/moments/" + momentIdResponse.momentId()))
                .body(momentIdResponse);
    }

    @GetMapping("/{momentId}")
    public ResponseEntity<MomentDetailResponse> readMomentById(
            @LoginMember Member member,
            @PathVariable @Min(value = 1L, message = "순간 기록 식별자는 양수로 이루어져야 합니다.") long momentId) {
        MomentDetailResponse momentDetailResponse = momentService.readMomentById(momentId, member);
        return ResponseEntity.ok().body(momentDetailResponse);
    }

    @PutMapping(path = "/{momentId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> updateMomentById(
            @LoginMember Member member,
            @PathVariable @Min(value = 1L, message = "순간 기록 식별자는 양수로 이루어져야 합니다.") long momentId,
            @Size(max = 5, message = "사진은 5장까지만 추가할 수 있어요.") @RequestPart("momentImageFiles") List<MultipartFile> momentImageFiles,
            @Valid @RequestPart(value = "data") MomentUpdateRequest request
    ) {
        momentService.updateMomentById(momentId, request, momentImageFiles, member);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{momentId}")
    public ResponseEntity<Void> deleteMomentById(
            @LoginMember Member member,
            @PathVariable @Min(value = 1L, message = "순간 기록 식별자는 양수로 이루어져야 합니다.") long momentId
    ) {
        momentService.deleteMomentById(momentId, member);
        return ResponseEntity.ok().build();
    }
}
