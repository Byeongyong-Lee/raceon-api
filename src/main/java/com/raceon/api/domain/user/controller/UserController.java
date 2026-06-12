package com.raceon.api.domain.user.controller;

import com.raceon.api.domain.user.dto.UserResponse;
import com.raceon.api.domain.user.service.UserService;
import com.raceon.api.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ApiResponse<UserResponse> getMe(Authentication authentication) {
        Long userIdx = Long.parseLong(authentication.getName());
        return ApiResponse.ok(userService.getMe(userIdx));
    }
}
