package com.staccato.auth.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.staccato.auth.controller.docs.AuthControllerV2Docs;
import com.staccato.auth.service.AuthService;
import com.staccato.auth.service.dto.request.LoginRequest;
import com.staccato.auth.service.dto.response.LoginResponseV2;
import com.staccato.config.log.annotation.Trace;
import lombok.RequiredArgsConstructor;

@Trace
@RestController
@RequiredArgsConstructor
@RequestMapping("/v2")
public class AuthControllerV2 implements AuthControllerV2Docs {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseV2> login(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponseV2 loginResponse = authService.login(loginRequest);
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/members")
    public ResponseEntity<LoginResponseV2> loginByCode(@RequestParam(name = "code") String code) {
        LoginResponseV2 loginResponse = authService.loginByCode(code);
        return ResponseEntity.ok(loginResponse);
    }
}
