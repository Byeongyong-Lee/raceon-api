package com.raceon.api.domain.auth.controller;

import com.raceon.api.domain.auth.dto.LoginResponse;
import com.raceon.api.domain.auth.dto.SocialLoginRequest;
import com.raceon.api.domain.auth.service.AuthService;
import com.raceon.api.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/kakao")
    public ApiResponse<LoginResponse> kakaoLogin(@RequestBody SocialLoginRequest request) {
        return ApiResponse.ok(authService.kakaoLogin(request));
    }

    @PostMapping("/naver")
    public ApiResponse<LoginResponse> naverLogin(@RequestBody SocialLoginRequest request) {
        return ApiResponse.ok(authService.naverLogin(request));
    }

    @PostMapping("/google")
    public ApiResponse<LoginResponse> googleLogin(@RequestBody SocialLoginRequest request) {
        return ApiResponse.ok(authService.googleLogin(request));
    }
}
