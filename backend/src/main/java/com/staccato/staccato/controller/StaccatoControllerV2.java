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
import com.staccato.staccato.controller.docs.StaccatoControllerV2Docs;
import com.staccato.staccato.service.StaccatoService;
import com.staccato.staccato.service.StaccatoShareService;
import com.staccato.staccato.service.dto.request.FeelingRequest;
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
@RequestMapping("/v2/staccatos")
@RequiredArgsConstructor
@Validated
public class StaccatoControllerV2 implements StaccatoControllerV2Docs {
    private final StaccatoService staccatoService;

    @GetMapping
    public ResponseEntity<StaccatoLocationResponsesV2> readAllStaccato(@LoginMember Member member) {
        StaccatoLocationResponsesV2 staccatoLocationResponses = staccatoService.readAllStaccato(member);
        return ResponseEntity.ok().body(staccatoLocationResponses);
    }
}
