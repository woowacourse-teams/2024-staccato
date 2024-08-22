package com.staccato.moment.controller;

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
import org.springframework.web.bind.annotation.RestController;

import com.staccato.config.auth.LoginMember;
import com.staccato.member.domain.Member;
import com.staccato.moment.controller.docs.MomentControllerDocs;
import com.staccato.moment.service.MomentService;
import com.staccato.moment.service.dto.request.FeelingRequest;
import com.staccato.moment.service.dto.request.MomentRequest;
import com.staccato.moment.service.dto.request.MomentUpdateRequest;
import com.staccato.moment.service.dto.response.MomentDetailResponse;
import com.staccato.moment.service.dto.response.MomentIdResponse;
import com.staccato.moment.service.dto.response.MomentLocationResponses;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/moments")
@RequiredArgsConstructor
@Validated
public class MomentController implements MomentControllerDocs {
    private final MomentService momentService;

    @PostMapping
    public ResponseEntity<MomentIdResponse> createMoment(
            @LoginMember Member member,
            @Valid @RequestBody MomentRequest momentRequest
    ) {
        MomentIdResponse momentIdResponse = momentService.createMoment(momentRequest, member);
        return ResponseEntity.created(URI.create("/moments/" + momentIdResponse.momentId()))
                .body(momentIdResponse);
    }

    @GetMapping
    public ResponseEntity<MomentLocationResponses> readAllMoment(@LoginMember Member member) {
        MomentLocationResponses momentLocationResponses = momentService.readAllMoment(member);
        return ResponseEntity.ok().body(momentLocationResponses);
    }

    @GetMapping("/{momentId}")
    public ResponseEntity<MomentDetailResponse> readMomentById(
            @LoginMember Member member,
            @PathVariable @Min(value = 1L, message = "스타카토 식별자는 양수로 이루어져야 합니다.") long momentId) {
        MomentDetailResponse momentDetailResponse = momentService.readMomentById(momentId, member);
        return ResponseEntity.ok().body(momentDetailResponse);
    }

    @PutMapping(path = "/{momentId}")
    public ResponseEntity<Void> updateMomentById(
            @LoginMember Member member,
            @PathVariable @Min(value = 1L, message = "스타카토 식별자는 양수로 이루어져야 합니다.") long momentId,
            @Valid @RequestBody MomentUpdateRequest request
    ) {
        momentService.updateMomentById(momentId, request, member);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{momentId}")
    public ResponseEntity<Void> deleteMomentById(
            @LoginMember Member member,
            @PathVariable @Min(value = 1L, message = "스타카토 식별자는 양수로 이루어져야 합니다.") long momentId
    ) {
        momentService.deleteMomentById(momentId, member);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{momentId}/feeling")
    public ResponseEntity<Void> updateMomentFeelingById(
            @LoginMember Member member,
            @PathVariable @Min(value = 1L, message = "스타카토 식별자는 양수로 이루어져야 합니다.") long momentId,
            @Valid @RequestBody FeelingRequest feelingRequest
    ) {
        momentService.updateMomentFeelingById(momentId, member, feelingRequest);
        return ResponseEntity.ok().build();
    }
}
