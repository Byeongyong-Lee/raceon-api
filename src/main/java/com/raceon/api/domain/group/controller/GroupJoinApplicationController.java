package com.raceon.api.domain.group.controller;

import com.raceon.api.domain.group.dto.ApplicationRequest;
import com.raceon.api.domain.group.dto.ApplicationResponse;
import com.raceon.api.domain.group.entity.GroupJoinApplication;
import com.raceon.api.domain.group.enums.ApplicationStatus;
import com.raceon.api.domain.group.service.GroupJoinApplicationService;
import com.raceon.api.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups/{groupIdx}/applications")
@RequiredArgsConstructor
public class GroupJoinApplicationController {

    private final GroupJoinApplicationService applicationService;

    // 가입 신청
    @PostMapping
    public ResponseEntity<ApiResponse<ApplicationResponse>> apply(Authentication auth,
                                                                   @PathVariable Long groupIdx,
                                                                   @RequestBody(required = false) ApplicationRequest request) {
        Long userIdx = Long.parseLong(auth.getName());
        String message = request != null ? request.getMessage() : null;
        GroupJoinApplication app = applicationService.apply(groupIdx, userIdx, message);
        return ResponseEntity.ok(ApiResponse.ok(new ApplicationResponse(app)));
    }

    // 내 신청 상태 확인
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<ApplicationResponse>> getMyApplication(Authentication auth,
                                                                              @PathVariable Long groupIdx) {
        Long userIdx = Long.parseLong(auth.getName());
        GroupJoinApplication app = applicationService.getMyApplication(groupIdx, userIdx);
        return ResponseEntity.ok(ApiResponse.ok(new ApplicationResponse(app)));
    }

    // 신청 목록 조회 (모임장/운영진)
    @GetMapping
    public ResponseEntity<ApiResponse<List<ApplicationResponse>>> getApplications(Authentication auth,
                                                                                   @PathVariable Long groupIdx,
                                                                                   @RequestParam(required = false) ApplicationStatus status) {
        Long userIdx = Long.parseLong(auth.getName());
        List<ApplicationResponse> list = applicationService.getApplications(groupIdx, userIdx, status)
                .stream().map(ApplicationResponse::new).toList();
        return ResponseEntity.ok(ApiResponse.ok(list));
    }

    // 승인
    @PostMapping("/{applicationIdx}/approve")
    public ResponseEntity<ApiResponse<Void>> approve(Authentication auth,
                                                      @PathVariable Long groupIdx,
                                                      @PathVariable Long applicationIdx) {
        Long userIdx = Long.parseLong(auth.getName());
        applicationService.approve(groupIdx, userIdx, applicationIdx);
        return ResponseEntity.ok(ApiResponse.ok());
    }

    // 거절
    @PostMapping("/{applicationIdx}/reject")
    public ResponseEntity<ApiResponse<Void>> reject(Authentication auth,
                                                     @PathVariable Long groupIdx,
                                                     @PathVariable Long applicationIdx) {
        Long userIdx = Long.parseLong(auth.getName());
        applicationService.reject(groupIdx, userIdx, applicationIdx);
        return ResponseEntity.ok(ApiResponse.ok());
    }
}
