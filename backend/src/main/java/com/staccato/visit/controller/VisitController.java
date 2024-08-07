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
import com.staccato.visit.service.dto.request.VisitRequest;
import com.staccato.visit.service.dto.request.VisitUpdateRequest;
import com.staccato.visit.service.dto.response.VisitDetailResponse;
import com.staccato.visit.service.dto.response.VisitIdResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/visits")
@RequiredArgsConstructor
@Validated
public class VisitController implements VisitControllerDocs {
    private final VisitService visitService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<VisitIdResponse> createVisit(
            @LoginMember Member member,
            @Valid @RequestPart(value = "data") VisitRequest visitRequest,
            @Size(max = 5, message = "사진은 5장까지만 추가할 수 있어요.") @RequestPart(value = "visitImageFiles") List<MultipartFile> visitImageFiles
    ) {
        VisitIdResponse visitIdResponse = visitService.createVisit(visitRequest, member);
        return ResponseEntity.created(URI.create("/visits/" + visitIdResponse.visitId()))
                .body(visitIdResponse);
    }

    @GetMapping("/{visitId}")
    public ResponseEntity<VisitDetailResponse> readVisitById(
            @LoginMember Member member,
            @PathVariable @Min(value = 1L, message = "방문 기록 식별자는 양수로 이루어져야 합니다.") long visitId) {
        VisitDetailResponse visitDetailResponse = visitService.readVisitById(visitId, member);
        return ResponseEntity.ok().body(visitDetailResponse);
    }

    @PutMapping(path = "/{visitId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> updateVisitById(
            @LoginMember Member member,
            @PathVariable @Min(value = 1L, message = "방문 기록 식별자는 양수로 이루어져야 합니다.") long visitId,
            @Size(max = 5, message = "사진은 5장까지만 추가할 수 있어요.") @RequestPart("visitImageFiles") List<MultipartFile> visitImageFiles,
            @Valid @RequestPart(value = "data") VisitUpdateRequest request
    ) {
        visitService.updateVisitById(visitId, request, visitImageFiles, member);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{visitId}")
    public ResponseEntity<Void> deleteVisitById(
            @LoginMember Member member,
            @PathVariable @Min(value = 1L, message = "방문 기록 식별자는 양수로 이루어져야 합니다.") long visitId
    ) {
        visitService.deleteVisitById(visitId, member);
        return ResponseEntity.ok().build();
    }
}
