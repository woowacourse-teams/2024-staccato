package com.staccato.auth.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.staccato.auth.controller.docs.AuthControllerDocs;
import com.staccato.auth.service.AuthService;
import com.staccato.auth.service.dto.request.LoginRequest;
import com.staccato.auth.service.dto.response.LoginResponse;
import com.staccato.config.log.annotation.Trace;
import lombok.RequiredArgsConstructor;

@Trace
@RestController
@RequiredArgsConstructor
public class AuthController implements AuthControllerDocs {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse loginResponse = authService.login(loginRequest);
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/members")
    public ResponseEntity<LoginResponse> loginByCode(@RequestParam(name = "code") String code) {
        LoginResponse loginResponse = authService.loginByCode(code);
        return ResponseEntity.ok(loginResponse);
    }
}
