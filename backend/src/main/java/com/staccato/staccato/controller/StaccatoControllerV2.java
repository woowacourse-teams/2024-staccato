package com.staccato.staccato.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.staccato.config.auth.LoginMember;
import com.staccato.config.log.annotation.Trace;
import com.staccato.member.domain.Member;
import com.staccato.staccato.controller.docs.StaccatoControllerV2Docs;
import com.staccato.staccato.service.StaccatoService;
import com.staccato.staccato.service.dto.request.ReadStaccatoRequest;
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
            @LoginMember Member member, @Validated @ModelAttribute ReadStaccatoRequest readStaccatoRequest
    ) {
        StaccatoLocationResponsesV2 staccatoLocationResponses = staccatoService.readAllStaccato(member, readStaccatoRequest);
        return ResponseEntity.ok().body(staccatoLocationResponses);
    }
}
