package com.staccato.visit.controller;

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
import com.staccato.visit.controller.docs.VisitControllerDocs;
import com.staccato.visit.service.VisitService;
import com.staccato.visit.service.dto.request.MomentRequest;
import com.staccato.visit.service.dto.request.MomentUpdateRequest;
import com.staccato.visit.service.dto.response.MomentDetailResponse;
import com.staccato.visit.service.dto.response.MomentIdResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/moments")
@RequiredArgsConstructor
@Validated
public class VisitController implements VisitControllerDocs {
    private final VisitService visitService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MomentIdResponse> createVisit(
            @LoginMember Member member,
            @Valid @RequestPart(value = "data") MomentRequest momentRequest,
            @Size(max = 5, message = "사진은 5장까지만 추가할 수 있어요.") @RequestPart(value = "momentImageFiles") List<MultipartFile> momentImageFiles
    ) {
        MomentIdResponse momentIdResponse = visitService.createVisit(momentRequest, momentImageFiles, member);
        return ResponseEntity.created(URI.create("/moments/" + momentIdResponse.momentId()))
                .body(momentIdResponse);
    }

    @GetMapping("/{momentId}")
    public ResponseEntity<MomentDetailResponse> readVisitById(
            @LoginMember Member member,
            @PathVariable @Min(value = 1L, message = "방문 기록 식별자는 양수로 이루어져야 합니다.") long momentId) {
        MomentDetailResponse momentDetailResponse = visitService.readVisitById(momentId, member);
        return ResponseEntity.ok().body(momentDetailResponse);
    }

    @PutMapping(path = "/{momentId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> updateVisitById(
            @LoginMember Member member,
            @PathVariable @Min(value = 1L, message = "방문 기록 식별자는 양수로 이루어져야 합니다.") long momentId,
            @Size(max = 5, message = "사진은 5장까지만 추가할 수 있어요.") @RequestPart("momentImageFiles") List<MultipartFile> momentImageFiles,
            @Valid @RequestPart(value = "data") MomentUpdateRequest request
    ) {
        visitService.updateVisitById(momentId, request, momentImageFiles, member);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{momentId}")
    public ResponseEntity<Void> deleteVisitById(
            @LoginMember Member member,
            @PathVariable @Min(value = 1L, message = "방문 기록 식별자는 양수로 이루어져야 합니다.") long momentId
    ) {
        visitService.deleteVisitById(momentId, member);
        return ResponseEntity.ok().build();
    }
}
