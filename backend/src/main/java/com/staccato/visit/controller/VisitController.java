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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.staccato.visit.controller.docs.VisitControllerDocs;
import com.staccato.visit.service.VisitService;
import com.staccato.visit.service.dto.request.VisitRequest;
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
            @Valid @RequestPart VisitRequest visitRequest,
            @Size(max = 5, message = "사진은 5장까지만 추가할 수 있어요.") @RequestPart(required = false) List<MultipartFile> visitImagesFile
    ) {
        VisitIdResponse visitIdResponse = visitService.createVisit(visitRequest);
        return ResponseEntity.created(URI.create("/visits/" + visitIdResponse.visitId()))
                .body(visitIdResponse);
    }

    @GetMapping("/{visitId}")
    public ResponseEntity<VisitDetailResponse> readVisitById(
            @PathVariable @Min(value = 1L, message = "방문 기록 식별자는 양수로 이루어져야 합니다.") Long visitId) {
        VisitDetailResponse visitDetailResponse = visitService.readVisitById(visitId);
        return ResponseEntity.ok().body(visitDetailResponse);
    }

    @DeleteMapping("/{visitId}")
    public ResponseEntity<Void> deleteVisitById(
            @PathVariable @Min(value = 1L, message = "방문 기록 식별자는 양수로 이루어져야 합니다.") long visitId
    ) {
        visitService.deleteVisitById(visitId);
        return ResponseEntity.ok().build();
    }
}
