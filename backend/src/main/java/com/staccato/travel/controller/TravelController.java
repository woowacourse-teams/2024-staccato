package com.staccato.travel.controller;

import java.net.URI;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.staccato.config.auth.MemberId;
import com.staccato.travel.service.TravelService;
import com.staccato.travel.service.dto.request.TravelRequest;
import com.staccato.travel.service.dto.response.TravelResponse;

import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequestMapping("/travels")
@RequiredArgsConstructor
public class TravelController {
    private final TravelService travelService;

    @PostMapping
    public ResponseEntity<Void> createTravel(@Valid @RequestBody TravelRequest travelRequest, @MemberId Long memberId) {
        TravelResponse travelResponse = travelService.createTravel(travelRequest, memberId);
        return ResponseEntity.created(URI.create("/travels/" + travelResponse.travelId())).build();
    }

    @PutMapping("/{travelId}")
    public ResponseEntity<Void> updateTravel(
            @PathVariable @Min(value = 1L, message = "여행 식별자는 양수로 이루어져야 합니다.") Long travelId,
            @Valid @RequestBody TravelRequest travelRequest,
            @MemberId Long memberId) {
        travelService.updateTravel(travelRequest, travelId);
        return ResponseEntity.ok().build();
    }
}
