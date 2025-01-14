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
import com.staccato.config.log.annotation.Trace;
import com.staccato.member.domain.Member;
import com.staccato.moment.controller.docs.MomentControllerDocs;
import com.staccato.moment.controller.docs.StaccatoControllerDocs;
import com.staccato.moment.service.MomentService;
import com.staccato.moment.service.dto.request.FeelingRequest;
import com.staccato.moment.service.dto.request.MomentRequest;
import com.staccato.moment.service.dto.request.StaccatoRequest;
import com.staccato.moment.service.dto.response.MomentDetailResponse;
import com.staccato.moment.service.dto.response.MomentIdResponse;
import com.staccato.moment.service.dto.response.MomentLocationResponses;
import com.staccato.moment.service.dto.response.StaccatoDetailResponse;
import com.staccato.moment.service.dto.response.StaccatoIdResponse;
import com.staccato.moment.service.dto.response.StaccatoLocationResponses;

import lombok.RequiredArgsConstructor;

@Trace
@RestController
@RequestMapping("/staccatos/v2")
@RequiredArgsConstructor
@Validated
public class StaccatoController implements StaccatoControllerDocs {
    private final MomentService momentService;

    @PostMapping
    public ResponseEntity<StaccatoIdResponse> createStaccato(
            @LoginMember Member member,
            @Valid @RequestBody StaccatoRequest staccatoRequest
    ) {
        MomentIdResponse momentIdResponse = momentService.createMoment(StaccatoDtoMapper.toMomentRequest(staccatoRequest), member);
        StaccatoIdResponse staccatoIdResponse = StaccatoDtoMapper.toStaccatoIdResponse(momentIdResponse);
        return ResponseEntity.created(URI.create("/staccatos/" + staccatoIdResponse.staccatoId()))
                .body(staccatoIdResponse);
    }

    @GetMapping
    public ResponseEntity<StaccatoLocationResponses> readAllStaccato(@LoginMember Member member) {
        MomentLocationResponses momentLocationResponses = momentService.readAllMoment(member);
        return ResponseEntity.ok().body(StaccatoDtoMapper.toStaccatoLocationResponses(momentLocationResponses));
    }

    @GetMapping("/{staccatoId}")
    public ResponseEntity<StaccatoDetailResponse> readStaccatoById(
            @LoginMember Member member,
            @PathVariable @Min(value = 1L, message = "스타카토 식별자는 양수로 이루어져야 합니다.") long staccatoId) {
        MomentDetailResponse momentDetailResponse = momentService.readMomentById(staccatoId, member);
        return ResponseEntity.ok().body(StaccatoDtoMapper.toStaccatoDetailResponse(momentDetailResponse));
    }

    @PutMapping(path = "/{staccatoId}")
    public ResponseEntity<Void> updateStaccatoById(
            @LoginMember Member member,
            @PathVariable @Min(value = 1L, message = "스타카토 식별자는 양수로 이루어져야 합니다.") long staccatoId,
            @Valid @RequestBody StaccatoRequest staccatoRequest
    ) {
        MomentRequest momentRequest = StaccatoDtoMapper.toMomentRequest(staccatoRequest);
        momentService.updateMomentById(staccatoId, momentRequest, member);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{staccatoId}")
    public ResponseEntity<Void> deleteStaccatoById(
            @LoginMember Member member,
            @PathVariable @Min(value = 1L, message = "스타카토 식별자는 양수로 이루어져야 합니다.") long staccatoId
    ) {
        momentService.deleteMomentById(staccatoId, member);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{staccatoId}/feeling")
    public ResponseEntity<Void> updateStaccatoFeelingById(
            @LoginMember Member member,
            @PathVariable @Min(value = 1L, message = "스타카토 식별자는 양수로 이루어져야 합니다.") long staccatoId,
            @Valid @RequestBody FeelingRequest feelingRequest
    ) {
        momentService.updateMomentFeelingById(staccatoId, member, feelingRequest);
        return ResponseEntity.ok().build();
    }
}
