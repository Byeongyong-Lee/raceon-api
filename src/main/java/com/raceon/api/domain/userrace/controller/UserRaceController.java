package com.raceon.api.domain.userrace.controller;

import com.raceon.api.domain.userrace.dto.UserRaceRecordUpdateRequest;
import com.raceon.api.domain.userrace.dto.UserRaceRegisterRequest;
import com.raceon.api.domain.userrace.dto.UserRaceResponse;
import com.raceon.api.domain.userrace.service.UserRaceService;
import com.raceon.api.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user-races")
@RequiredArgsConstructor
public class UserRaceController {

    private final UserRaceService userRaceService;

    @PostMapping
    public ApiResponse<UserRaceResponse> register(
            Authentication authentication,
            @RequestBody UserRaceRegisterRequest request) {
        Long userIdx = Long.parseLong(authentication.getName());
        return ApiResponse.ok(userRaceService.register(userIdx, request));
    }

    @DeleteMapping("/{userRaceIdx}")
    public ApiResponse<Void> cancel(
            Authentication authentication,
            @PathVariable Long userRaceIdx) {
        Long userIdx = Long.parseLong(authentication.getName());
        userRaceService.cancel(userIdx, userRaceIdx);
        return ApiResponse.ok();
    }

    @GetMapping("/me")
    public ApiResponse<List<UserRaceResponse>> getMyRaces(Authentication authentication) {
        Long userIdx = Long.parseLong(authentication.getName());
        return ApiResponse.ok(userRaceService.getMyRaces(userIdx));
    }

    @PatchMapping("/{userRaceIdx}/record")
    public ApiResponse<UserRaceResponse> updateRecord(
            Authentication authentication,
            @PathVariable Long userRaceIdx,
            @RequestBody UserRaceRecordUpdateRequest request) {
        Long userIdx = Long.parseLong(authentication.getName());
        return ApiResponse.ok(userRaceService.updateRecord(userIdx, userRaceIdx, request));
    }
}
