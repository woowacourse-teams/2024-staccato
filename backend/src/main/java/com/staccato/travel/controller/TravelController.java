package com.staccato.travel.controller;

import java.net.URI;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.staccato.config.auth.LoginMember;
import com.staccato.member.domain.Member;
import com.staccato.travel.controller.docs.TravelControllerDocs;
import com.staccato.travel.service.TravelService;
import com.staccato.travel.service.dto.request.TravelRequest;
import com.staccato.travel.service.dto.response.TravelDetailResponse;
import com.staccato.travel.service.dto.response.TravelResponses;

import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequestMapping("/travels")
@RequiredArgsConstructor
public class TravelController implements TravelControllerDocs {
    private final TravelService travelService;

    @PostMapping
    public ResponseEntity<Void> createTravel(@Valid @RequestBody TravelRequest travelRequest, @LoginMember Member member) {
        long travelId = travelService.createTravel(travelRequest, member);
        return ResponseEntity.created(URI.create("/travels/" + travelId)).build();
    }

    @GetMapping
    public ResponseEntity<TravelResponses> readAllTravels(
            @LoginMember Member member,
            @RequestParam(value = "year", required = false) Integer year
    ) {
        TravelResponses travelResponses = travelService.readAllTravels(member, year);
        return ResponseEntity.ok(travelResponses);
    }

    @GetMapping("/{travelId}")
    public ResponseEntity<TravelDetailResponse> readTravel(
            @LoginMember Member member,
            @PathVariable @Min(value = 1L, message = "여행 식별자는 양수로 이루어져야 합니다.") long travelId) {
        return ResponseEntity.ok(travelService.readTravelById(travelId));
    }

    @PutMapping("/{travelId}")
    public ResponseEntity<Void> updateTravel(
            @PathVariable @Min(value = 1L, message = "여행 식별자는 양수로 이루어져야 합니다.") long travelId,
            @Valid @RequestBody TravelRequest travelRequest,
            @LoginMember Member member) {
        travelService.updateTravel(travelRequest, travelId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{travelId}")
    public ResponseEntity<Void> deleteTravel(
            @PathVariable @Min(value = 1L, message = "여행 식별자는 양수로 이루어져야 합니다.") long travelId,
            @LoginMember Member member) {
        travelService.deleteTravel(travelId);
        return ResponseEntity.ok().build();
    }
}
