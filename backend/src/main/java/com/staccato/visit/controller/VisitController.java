package com.staccato.visit.controller;

import java.net.URI;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.staccato.visit.service.VisitService;
import com.staccato.visit.service.dto.request.VisitRequest;
import com.staccato.visit.service.dto.response.VisitDetailResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/visits")
@RequiredArgsConstructor
@Validated
public class VisitController {
    private final VisitService visitService;

    @PostMapping
    public ResponseEntity<Void> createVisit(@Valid @RequestBody VisitRequest visitRequest) {
        long visitId = visitService.createVisit(visitRequest);
        return ResponseEntity.created(URI.create("/visits/" + visitId)).build();
    }

    @DeleteMapping("/{visitId}")
    public ResponseEntity<Void> deleteById(
            @PathVariable @Min(value = 1L, message = "방문 기록 식별자는 양수로 이루어져야 합니다.") Long visitId) {
        visitService.deleteById(visitId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{visitId}")
    public ResponseEntity<VisitDetailResponse> getVisitById(
            @PathVariable @Min(value = 1L, message = "방문 기록 식별자는 양수로 이루어져야 합니다.") Long visitId) {
        VisitDetailResponse visitDetailResponse = visitService.getById(visitId);
        return ResponseEntity.ok().body(visitDetailResponse);
    }
}
