package com.staccato.travel.controller;

import java.net.URI;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.staccato.config.auth.LoginMember;
import com.staccato.member.domain.Member;
import com.staccato.travel.controller.docs.TravelControllerDocs;
import com.staccato.travel.service.TravelService;
import com.staccato.travel.service.dto.request.TravelRequest;
import com.staccato.travel.service.dto.response.TravelDetailResponse;
import com.staccato.travel.service.dto.response.TravelIdResponse;
import com.staccato.travel.service.dto.response.TravelResponses;

import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequestMapping("/memories")
@RequiredArgsConstructor
public class TravelController implements TravelControllerDocs {
    private final TravelService travelService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<TravelIdResponse> createTravel(
            @Valid @RequestPart(value = "data") TravelRequest travelRequest,
            @RequestPart(value = "memoryThumbnailFile", required = false) MultipartFile travelThumbnailFile,
            @LoginMember Member member
    ) {
        TravelIdResponse travelIdResponse = travelService.createTravel(travelRequest, travelThumbnailFile, member);
        return ResponseEntity.created(URI.create("/memories/" + travelIdResponse.memoryId())).body(travelIdResponse);
    }

    @GetMapping
    public ResponseEntity<TravelResponses> readAllTravels(
            @LoginMember Member member,
            @RequestParam(value = "year", required = false) Integer year
    ) {
        TravelResponses travelResponses = travelService.readAllTravels(member, year);
        return ResponseEntity.ok(travelResponses);
    }

    @GetMapping("/{memoryId}")
    public ResponseEntity<TravelDetailResponse> readTravel(
            @LoginMember Member member,
            @PathVariable @Min(value = 1L, message = "여행 식별자는 양수로 이루어져야 합니다.") long memoryId) {
        TravelDetailResponse travelDetailResponse = travelService.readTravelById(memoryId, member);
        return ResponseEntity.ok(travelDetailResponse);
    }

    @PutMapping(path = "/{memoryId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> updateTravel(
            @PathVariable @Min(value = 1L, message = "여행 식별자는 양수로 이루어져야 합니다.") long memoryId,
            @Valid @RequestPart(value = "data") TravelRequest travelRequest,
            @RequestPart(value = "memoryThumbnailFile", required = false) MultipartFile travelThumbnailFile,
            @LoginMember Member member) {
        travelService.updateTravel(travelRequest, memoryId, travelThumbnailFile, member);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{memoryId}")
    public ResponseEntity<Void> deleteTravel(
            @PathVariable @Min(value = 1L, message = "여행 식별자는 양수로 이루어져야 합니다.") long memoryId,
            @LoginMember Member member) {
        travelService.deleteTravel(memoryId, member);
        return ResponseEntity.ok().build();
    }
}
