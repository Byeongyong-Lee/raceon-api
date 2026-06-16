package com.raceon.api.domain.user.controller;

import com.raceon.api.domain.user.dto.UserResponse;
import com.raceon.api.domain.user.service.UserService;
import com.raceon.api.global.response.ApiResponse;
import com.raceon.api.global.upload.ImageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @PostMapping(value = "/me/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<ImageResponse> uploadProfileImage(
            Authentication authentication,
            @RequestPart("file") MultipartFile file) {
        Long userIdx = Long.parseLong(authentication.getName());
        return ApiResponse.ok(userService.uploadProfileImage(userIdx, file));
    }
}
