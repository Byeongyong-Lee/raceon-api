package com.raceon.api.domain.group.controller;

import com.raceon.api.domain.group.dto.GroupCreateRequest;
import com.raceon.api.domain.group.dto.GroupResponse;
import com.raceon.api.domain.group.dto.GroupUpdateRequest;
import com.raceon.api.domain.group.entity.Group;
import com.raceon.api.domain.group.entity.GroupMember;
import com.raceon.api.domain.group.enums.GroupRole;
import com.raceon.api.domain.group.service.GroupService;
import com.raceon.api.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    /** 전체 모임 탐색 — 인증 선택 (비로그인 시 role=null) */
    @GetMapping
    public ResponseEntity<ApiResponse<List<GroupResponse>>> getGroups(
            Authentication auth,
            @RequestParam(required = false) String areaCode,
            @RequestParam(required = false) String keyword) {
        Long userIdx = auth != null ? Long.parseLong(auth.getName()) : null;
        List<Group> groups = groupService.searchGroups(keyword, areaCode);
        List<GroupResponse> list = groups.stream()
                .map(g -> {
                    long count = groupService.getMemberCount(g.getGroupIdx());
                    GroupRole role = userIdx != null ? groupService.getUserRole(g.getGroupIdx(), userIdx) : null;
                    return new GroupResponse(g, role, count);
                })
                .toList();
        return ResponseEntity.ok(ApiResponse.ok(list));
    }

    /** 모임 생성 — JSON (이미지는 생성 후 /{groupIdx}/image 로 별도 업로드) */
    @PostMapping
    public ResponseEntity<ApiResponse<GroupResponse>> create(
            Authentication auth,
            @RequestBody GroupCreateRequest request) {
        Long userIdx = Long.parseLong(auth.getName());
        Group group = groupService.create(userIdx, request.getName(), request.getDescription(), null,
                request.getGroupMembers(), request.getManagerMembers(), request.getAreaCode(),
                request.getTag1(), request.getTag2(), request.getTag3(), request.getTag4(), request.getTag5());
        return ResponseEntity.ok(ApiResponse.ok(new GroupResponse(group, GroupRole.OWNER, 1)));
    }

    /** 모임 프로필 이미지 업로드 */
    @PostMapping(value = "/{groupIdx}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Void>> uploadImage(
            Authentication auth,
            @PathVariable Long groupIdx,
            @RequestPart("file") MultipartFile imageFile) {
        Long userIdx = Long.parseLong(auth.getName());
        groupService.updateProfileImage(userIdx, groupIdx, imageFile);
        return ResponseEntity.ok(ApiResponse.ok());
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<List<GroupResponse>>> getMyGroups(Authentication auth) {
        Long userIdx = Long.parseLong(auth.getName());
        List<GroupMember> memberships = groupService.getMyMemberships(userIdx);
        List<GroupResponse> list = memberships.stream()
                .map(m -> {
                    try {
                        Group group = groupService.getGroup(m.getGroupIdx());
                        long count = groupService.getMemberCount(m.getGroupIdx());
                        return new GroupResponse(group, m.getRole(), count);
                    } catch (IllegalArgumentException e) {
                        return null;
                    }
                })
                .filter(r -> r != null)
                .toList();
        return ResponseEntity.ok(ApiResponse.ok(list));
    }

    @GetMapping("/{groupIdx}")
    public ResponseEntity<ApiResponse<GroupResponse>> getGroup(Authentication auth,
                                                                @PathVariable Long groupIdx) {
        Long userIdx = Long.parseLong(auth.getName());
        groupService.validateMember(groupIdx, userIdx);
        Group group = groupService.getGroup(groupIdx);
        long count = groupService.getMemberCount(groupIdx);
        GroupRole role = groupService.getUserRole(groupIdx, userIdx);
        return ResponseEntity.ok(ApiResponse.ok(new GroupResponse(group, role, count)));
    }

    @PatchMapping("/{groupIdx}")
    public ResponseEntity<ApiResponse<Void>> update(
            Authentication auth,
            @PathVariable Long groupIdx,
            @RequestBody GroupUpdateRequest request) {
        Long userIdx = Long.parseLong(auth.getName());
        groupService.update(userIdx, groupIdx, request.getName(), request.getDescription(),
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
