package com.staccato.staccato.controller;

import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.staccato.config.auth.LoginMember;
import com.staccato.config.log.annotation.Trace;
import com.staccato.member.domain.Member;
import com.staccato.staccato.controller.docs.StaccatoControllerV2Docs;
import com.staccato.staccato.service.StaccatoService;
import com.staccato.staccato.service.dto.request.StaccatoLocationRangeRequest;
import com.staccato.staccato.service.dto.response.StaccatoDetailResponseV2;
import com.staccato.staccato.service.dto.response.StaccatoLocationResponsesV2;
import lombok.RequiredArgsConstructor;

@Trace
@RestController
@RequestMapping("/v2/staccatos")
@RequiredArgsConstructor
@Validated
public class StaccatoControllerV2 implements StaccatoControllerV2Docs {
    private final StaccatoService staccatoService;

    @GetMapping
    public ResponseEntity<StaccatoLocationResponsesV2> readAllStaccato(
            @LoginMember Member member, @Validated @ModelAttribute StaccatoLocationRangeRequest staccatoLocationRangeRequest
    ) {
        StaccatoLocationResponsesV2 staccatoLocationResponses = staccatoService.readAllStaccato(member, staccatoLocationRangeRequest);
        return ResponseEntity.ok().body(staccatoLocationResponses);
    }

    @GetMapping("/{staccatoId}")
    public ResponseEntity<StaccatoDetailResponseV2> readStaccatoById(
            @LoginMember Member member,
            @PathVariable @Min(value = 1L, message = "스타카토 식별자는 양수로 이루어져야 합니다.") long staccatoId) {
        StaccatoDetailResponseV2 staccatoDetailResponse = staccatoService.readStaccatoById(staccatoId, member);
        return ResponseEntity.ok().body(staccatoDetailResponse);
    }
}
