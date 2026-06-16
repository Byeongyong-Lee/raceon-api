package com.raceon.api.domain.group.controller;

import com.raceon.api.domain.group.dto.GroupMemberResponse;
import com.raceon.api.domain.group.dto.PermissionUpdateRequest;
import com.raceon.api.domain.group.dto.RoleChangeRequest;
import com.raceon.api.domain.group.entity.GroupManagerPermission;
import com.raceon.api.domain.group.entity.GroupMember;
import com.raceon.api.domain.group.service.GroupMemberService;
import com.raceon.api.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups/{groupIdx}/members")
@RequiredArgsConstructor
public class GroupMemberController {

    private final GroupMemberService groupMemberService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<GroupMemberResponse>>> getMembers(Authentication auth,
                                                                              @PathVariable Long groupIdx) {
        Long userIdx = Long.parseLong(auth.getName());
        List<GroupMember> members = groupMemberService.getMembers(groupIdx, userIdx);
        List<GroupMemberResponse> list = members.stream().map(GroupMemberResponse::new).toList();
        return ResponseEntity.ok(ApiResponse.ok(list));
    }

    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<Void>> leave(Authentication auth,
                                                    @PathVariable Long groupIdx) {
        Long userIdx = Long.parseLong(auth.getName());
        groupMemberService.leave(groupIdx, userIdx);
        return ResponseEntity.ok(ApiResponse.ok());
    }

    @DeleteMapping("/{targetUserIdx}")
    public ResponseEntity<ApiResponse<Void>> kick(Authentication auth,
                                                   @PathVariable Long groupIdx,
                                                   @PathVariable Long targetUserIdx) {
        Long userIdx = Long.parseLong(auth.getName());
        groupMemberService.kick(groupIdx, userIdx, targetUserIdx);
        return ResponseEntity.ok(ApiResponse.ok());
    }

    @PatchMapping("/{targetUserIdx}/role")
    public ResponseEntity<ApiResponse<Void>> changeRole(Authentication auth,
                                                         @PathVariable Long groupIdx,
                                                         @PathVariable Long targetUserIdx,
                                                         @RequestBody RoleChangeRequest request) {
        Long userIdx = Long.parseLong(auth.getName());
        groupMemberService.changeRole(groupIdx, userIdx, targetUserIdx, request.getRole());
        return ResponseEntity.ok(ApiResponse.ok());
    }

    @PutMapping("/{targetUserIdx}/permissions")
    public ResponseEntity<ApiResponse<GroupManagerPermission>> updatePermission(Authentication auth,
                                                                                  @PathVariable Long groupIdx,
                                                                                  @PathVariable Long targetUserIdx,
                                                                                  @RequestBody PermissionUpdateRequest req) {
        Long userIdx = Long.parseLong(auth.getName());
        GroupManagerPermission perm = groupMemberService.updatePermission(
                groupIdx, userIdx, targetUserIdx,
                req.getCanManageBoard(), req.getCanManageMembers(),
                req.getCanManageRace(), req.getCanManageMeetup());
        return ResponseEntity.ok(ApiResponse.ok(perm));
    }

    @GetMapping("/{targetUserIdx}/permissions")
    public ResponseEntity<ApiResponse<GroupManagerPermission>> getPermission(Authentication auth,
                                                                               @PathVariable Long groupIdx,
                                                                               @PathVariable Long targetUserIdx) {
        Long userIdx = Long.parseLong(auth.getName());
        GroupManagerPermission perm = groupMemberService.getPermission(groupIdx, userIdx, targetUserIdx);
        return ResponseEntity.ok(ApiResponse.ok(perm));
    }
}
