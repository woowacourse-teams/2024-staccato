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
import com.staccato.travel.service.dto.request.MemoryRequest;
import com.staccato.travel.service.dto.response.MemoryDetailResponse;
import com.staccato.travel.service.dto.response.MemoryIdResponse;
import com.staccato.travel.service.dto.response.MemoryResponses;

import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequestMapping("/memories")
@RequiredArgsConstructor
public class TravelController implements TravelControllerDocs {
    private final TravelService travelService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MemoryIdResponse> createTravel(
            @Valid @RequestPart(value = "data") MemoryRequest memoryRequest,
            @RequestPart(value = "memoryThumbnailFile", required = false) MultipartFile travelThumbnailFile,
            @LoginMember Member member
    ) {
        MemoryIdResponse memoryIdResponse = travelService.createTravel(memoryRequest, travelThumbnailFile, member);
        return ResponseEntity.created(URI.create("/memories/" + memoryIdResponse.memoryId())).body(memoryIdResponse);
    }

    @GetMapping
    public ResponseEntity<MemoryResponses> readAllTravels(
            @LoginMember Member member,
            @RequestParam(value = "year", required = false) Integer year
    ) {
        MemoryResponses memoryResponses = travelService.readAllTravels(member, year);
        return ResponseEntity.ok(memoryResponses);
    }

    @GetMapping("/{memoryId}")
    public ResponseEntity<MemoryDetailResponse> readTravel(
            @LoginMember Member member,
            @PathVariable @Min(value = 1L, message = "여행 식별자는 양수로 이루어져야 합니다.") long memoryId) {
        MemoryDetailResponse memoryDetailResponse = travelService.readTravelById(memoryId, member);
        return ResponseEntity.ok(memoryDetailResponse);
    }

    @PutMapping(path = "/{memoryId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> updateTravel(
            @PathVariable @Min(value = 1L, message = "여행 식별자는 양수로 이루어져야 합니다.") long memoryId,
            @Valid @RequestPart(value = "data") MemoryRequest memoryRequest,
            @RequestPart(value = "memoryThumbnailFile", required = false) MultipartFile travelThumbnailFile,
            @LoginMember Member member) {
        travelService.updateTravel(memoryRequest, memoryId, travelThumbnailFile, member);
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
