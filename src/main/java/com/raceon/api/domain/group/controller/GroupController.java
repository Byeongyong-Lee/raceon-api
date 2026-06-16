package com.raceon.api.domain.group.controller;

import com.raceon.api.domain.group.dto.GroupCreateRequest;
import com.raceon.api.domain.group.dto.GroupResponse;
import com.raceon.api.domain.group.dto.GroupUpdateRequest;
import com.raceon.api.domain.group.entity.Group;
import com.raceon.api.domain.group.service.GroupService;
import com.raceon.api.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    @PostMapping
    public ResponseEntity<ApiResponse<GroupResponse>> create(Authentication auth,
                                                              @RequestBody GroupCreateRequest request) {
        Long userIdx = Long.parseLong(auth.getName());
        Group group = groupService.create(userIdx, request.getName(), request.getDescription(), request.getProfileImage(),
                request.getGroupMembers(), request.getManagerMembers(), request.getAreaCode(),
                request.getTag1(), request.getTag2(), request.getTag3(), request.getTag4(), request.getTag5());
        return ResponseEntity.ok(ApiResponse.ok(new GroupResponse(group)));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<List<GroupResponse>>> getMyGroups(Authentication auth) {
        Long userIdx = Long.parseLong(auth.getName());
        List<GroupResponse> list = groupService.getMyGroups(userIdx).stream()
                .map(GroupResponse::new).toList();
        return ResponseEntity.ok(ApiResponse.ok(list));
    }

    @GetMapping("/{groupIdx}")
    public ResponseEntity<ApiResponse<GroupResponse>> getGroup(Authentication auth,
                                                                @PathVariable Long groupIdx) {
        Long userIdx = Long.parseLong(auth.getName());
        groupService.validateMember(groupIdx, userIdx);
        Group group = groupService.getGroup(groupIdx);
        return ResponseEntity.ok(ApiResponse.ok(new GroupResponse(group)));
    }

    @PatchMapping("/{groupIdx}")
    public ResponseEntity<ApiResponse<Void>> update(Authentication auth,
                                                     @PathVariable Long groupIdx,
                                                     @RequestBody GroupUpdateRequest request) {
        Long userIdx = Long.parseLong(auth.getName());
        groupService.update(userIdx, groupIdx, request.getName(), request.getDescription(), request.getProfileImage(),
                request.getGroupMembers(), request.getManagerMembers(), request.getAreaCode(),
                request.getTag1(), request.getTag2(), request.getTag3(), request.getTag4(), request.getTag5());
        return ResponseEntity.ok(ApiResponse.ok());
    }

    @DeleteMapping("/{groupIdx}")
    public ResponseEntity<ApiResponse<Void>> delete(Authentication auth,
                                                     @PathVariable Long groupIdx) {
        Long userIdx = Long.parseLong(auth.getName());
        groupService.delete(userIdx, groupIdx);
        return ResponseEntity.ok(ApiResponse.ok());
    }

}
