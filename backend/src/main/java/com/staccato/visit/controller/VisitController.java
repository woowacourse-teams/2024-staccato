package com.staccato.visit.controller;

import java.net.URI;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.staccato.visit.service.VisitService;
import com.staccato.visit.service.dto.request.VisitRequest;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/visits")
@RequiredArgsConstructor
public class VisitController {
    private final VisitService visitService;

    @PostMapping
    public ResponseEntity<Void> createVisit(@Valid @RequestBody VisitRequest visitRequest) {
        long visitId = visitService.createVisit(visitRequest);
        return ResponseEntity.created(URI.create("/visits/" + visitId)).build();
    }
}
