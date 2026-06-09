package com.raceon.api.domain.user.controller;

import com.raceon.api.domain.user.dto.UserResponse;
import com.raceon.api.domain.user.service.UserService;
import com.raceon.api.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ApiResponse<UserResponse> getMe(@AuthenticationPrincipal UserDetails userDetails) {
        Long userIdx = Long.parseLong(userDetails.getUsername());
        return ApiResponse.ok(userService.getMe(userIdx));
    }
}
