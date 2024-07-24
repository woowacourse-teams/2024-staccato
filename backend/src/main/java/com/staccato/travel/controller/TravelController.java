package com.staccato.travel.controller;

import java.net.URI;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.staccato.config.auth.MemberId;
import com.staccato.travel.service.TravelService;
import com.staccato.travel.service.dto.request.TravelRequest;
import com.staccato.travel.service.dto.response.TravelResponses;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/travels")
@RequiredArgsConstructor
public class TravelController {
    private final TravelService travelService;

    @PostMapping
    public ResponseEntity<Void> createTravel(@Valid @RequestBody TravelRequest travelRequest, @MemberId Long memberId) {
        long travelId = travelService.createTravel(travelRequest, memberId);
        return ResponseEntity.created(URI.create("/travels/" + travelId)).build();
    }

    @GetMapping
    public ResponseEntity<TravelResponses> readAllTravels(
            @MemberId Long memberId,
            @RequestParam(value = "year", required = false) Integer year) {
        return ResponseEntity.ok(travelService.readAllTravels(memberId, year));
    }
}
