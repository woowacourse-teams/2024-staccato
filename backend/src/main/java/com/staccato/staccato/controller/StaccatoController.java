package com.staccato.staccato.controller;

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
import com.staccato.staccato.controller.docs.StaccatoControllerDocs;
import com.staccato.staccato.service.StaccatoService;
import com.staccato.staccato.service.StaccatoShareService;
import com.staccato.staccato.service.dto.request.FeelingRequest;
import com.staccato.staccato.service.dto.request.StaccatoLocationRangeRequest;
import com.staccato.staccato.service.dto.request.StaccatoRequest;
import com.staccato.staccato.service.dto.response.StaccatoDetailResponse;
import com.staccato.staccato.service.dto.response.StaccatoIdResponse;
import com.staccato.staccato.service.dto.response.StaccatoLocationResponses;
import com.staccato.staccato.service.dto.response.StaccatoLocationResponsesV2;
import com.staccato.staccato.service.dto.response.StaccatoShareLinkResponse;
import com.staccato.staccato.service.dto.response.StaccatoSharedResponse;

import lombok.RequiredArgsConstructor;

@Trace
@RestController
@RequestMapping("/staccatos")
@RequiredArgsConstructor
@Validated
public class StaccatoController implements StaccatoControllerDocs {
    private final StaccatoService staccatoService;
    private final StaccatoShareService staccatoShareService;

    @PostMapping
    public ResponseEntity<StaccatoIdResponse> createStaccato(
            @LoginMember Member member,
            @Valid @RequestBody StaccatoRequest staccatoRequest
    ) {
        StaccatoIdResponse staccatoIdResponse = staccatoService.createStaccato(staccatoRequest, member);
        return ResponseEntity.created(URI.create("/staccatos/" + staccatoIdResponse.staccatoId()))
                .body(staccatoIdResponse);
    }

    @GetMapping
    public ResponseEntity<StaccatoLocationResponses> readAllStaccato(@LoginMember Member member) {
        StaccatoLocationResponsesV2 staccatoLocationResponses = staccatoService.readAllStaccato(member, StaccatoLocationRangeRequest.empty());
        return ResponseEntity.ok().body(staccatoLocationResponses.toStaccatoLocationResponses());
    }

    @GetMapping("/{staccatoId}")
    public ResponseEntity<StaccatoDetailResponse> readStaccatoById(
            @LoginMember Member member,
            @PathVariable @Min(value = 1L, message = "스타카토 식별자는 양수로 이루어져야 합니다.") long staccatoId) {
        StaccatoDetailResponse staccatoDetailResponse = staccatoService.readStaccatoById(staccatoId, member);
        return ResponseEntity.ok().body(staccatoDetailResponse);
    }

    @PutMapping(path = "/{staccatoId}")
    public ResponseEntity<Void> updateStaccatoById(
            @LoginMember Member member,
            @PathVariable @Min(value = 1L, message = "스타카토 식별자는 양수로 이루어져야 합니다.") long staccatoId,
            @Valid @RequestBody StaccatoRequest staccatoRequest
    ) {
        staccatoService.updateStaccatoById(staccatoId, staccatoRequest, member);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{staccatoId}")
    public ResponseEntity<Void> deleteStaccatoById(
            @LoginMember Member member,
            @PathVariable @Min(value = 1L, message = "스타카토 식별자는 양수로 이루어져야 합니다.") long staccatoId
    ) {
        staccatoService.deleteStaccatoById(staccatoId, member);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{staccatoId}/feeling")
    public ResponseEntity<Void> updateStaccatoFeelingById(
            @LoginMember Member member,
            @PathVariable @Min(value = 1L, message = "스타카토 식별자는 양수로 이루어져야 합니다.") long staccatoId,
            @Valid @RequestBody FeelingRequest feelingRequest
    ) {
        staccatoService.updateStaccatoFeelingById(staccatoId, member, feelingRequest);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{staccatoId}/share")
    public ResponseEntity<StaccatoShareLinkResponse> shareStaccato(
            @LoginMember Member member,
            @PathVariable @Min(value = 1L, message = "스타카토 식별자는 양수로 이루어져야 합니다.") long staccatoId
    ) {
        StaccatoShareLinkResponse staccatoShareLinkResponse = staccatoShareService.createStaccatoShareLink(staccatoId, member);
        return ResponseEntity.created(URI.create("/staccatos/shared/" + staccatoShareLinkResponse.token()))
                .body(staccatoShareLinkResponse);
    }

    @GetMapping("/shared/{token}")
    public ResponseEntity<StaccatoSharedResponse> readSharedStaccatoByToken(@PathVariable String token) {
        StaccatoSharedResponse staccatoSharedResponse = staccatoShareService.readSharedStaccatoByToken(token);
        return ResponseEntity.ok().body(staccatoSharedResponse);
    }
}
