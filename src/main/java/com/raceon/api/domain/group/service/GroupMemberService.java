package com.raceon.api.domain.group.service;

import com.raceon.api.domain.group.entity.GroupMember;
import com.raceon.api.domain.group.entity.GroupManagerPermission;
import com.raceon.api.domain.group.enums.GroupRole;
import com.raceon.api.domain.group.repository.GroupMemberRepository;
import com.raceon.api.domain.group.repository.GroupManagerPermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GroupMemberService {

    private final GroupMemberRepository groupMemberRepository;
    private final GroupManagerPermissionRepository permissionRepository;
    private final GroupService groupService;

    public List<GroupMember> getMembers(Long groupIdx, Long requestUserIdx) {
        groupService.validateMember(groupIdx, requestUserIdx);
        return groupMemberRepository.findByGroupIdxAndDelAt(groupIdx, "N");
    }

    @Transactional
    public void kick(Long groupIdx, Long requestUserIdx, Long targetUserIdx) {
        GroupMember requester = groupMemberRepository.findByGroupIdxAndUserIdxAndDelAt(groupIdx, requestUserIdx, "N")
                .orElseThrow(() -> new IllegalArgumentException("모임 멤버가 아닙니다."));

        if (requester.getRole() == GroupRole.MEMBER) {
            throw new IllegalArgumentException("권한이 없습니다.");
        }
        if (requester.getRole() == GroupRole.MANAGER) {
            GroupManagerPermission perm = permissionRepository.findByGroupIdxAndUserIdx(groupIdx, requestUserIdx)
                    .orElseThrow(() -> new IllegalArgumentException("권한 정보가 없습니다."));
            if (!"Y".equals(perm.getCanManageMembers())) {
                throw new IllegalArgumentException("멤버 관리 권한이 없습니다.");
            }
        }

        GroupMember target = groupMemberRepository.findByGroupIdxAndUserIdxAndDelAt(groupIdx, targetUserIdx, "N")
                .orElseThrow(() -> new IllegalArgumentException("대상 멤버를 찾을 수 없습니다."));
        if (target.getRole() == GroupRole.OWNER) {
            throw new IllegalArgumentException("모임장은 강퇴할 수 없습니다.");
        }
        target.leave();
        permissionRepository.deleteByGroupIdxAndUserIdx(groupIdx, targetUserIdx);
    }

    @Transactional
    public void leave(Long groupIdx, Long userIdx) {
        GroupMember member = groupMemberRepository.findByGroupIdxAndUserIdxAndDelAt(groupIdx, userIdx, "N")
                .orElseThrow(() -> new IllegalArgumentException("모임 멤버가 아닙니다."));
        if (member.getRole() == GroupRole.OWNER) {
            throw new IllegalArgumentException("모임장은 탈퇴할 수 없습니다. 모임을 삭제하거나 모임장을 위임하세요.");
        }
        member.leave();
        permissionRepository.deleteByGroupIdxAndUserIdx(groupIdx, userIdx);
    }

    @Transactional
    public void changeRole(Long groupIdx, Long ownerIdx, Long targetUserIdx, GroupRole newRole) {
        groupService.validateOwner(groupIdx, ownerIdx);
        if (newRole == GroupRole.OWNER) {
            throw new IllegalArgumentException("모임장 권한은 이 API로 변경할 수 없습니다.");
        }
        GroupMember target = groupMemberRepository.findByGroupIdxAndUserIdxAndDelAt(groupIdx, targetUserIdx, "N")
                .orElseThrow(() -> new IllegalArgumentException("대상 멤버를 찾을 수 없습니다."));
        if (target.getRole() == GroupRole.OWNER) {
            throw new IllegalArgumentException("모임장의 역할은 변경할 수 없습니다.");
        }
        target.changeRole(newRole);
        if (newRole == GroupRole.MEMBER) {
            permissionRepository.deleteByGroupIdxAndUserIdx(groupIdx, targetUserIdx);
        }
    }

    @Transactional
    public GroupManagerPermission updatePermission(Long groupIdx, Long ownerIdx, Long targetUserIdx,
                                                    String canManageBoard, String canManageMembers,
                                                    String canManageRace, String canManageMeetup) {
        groupService.validateOwner(groupIdx, ownerIdx);
        GroupMember target = groupMemberRepository.findByGroupIdxAndUserIdxAndDelAt(groupIdx, targetUserIdx, "N")
                .orElseThrow(() -> new IllegalArgumentException("대상 멤버를 찾을 수 없습니다."));
        if (target.getRole() != GroupRole.MANAGER) {
            throw new IllegalArgumentException("운영진에게만 세부 권한을 설정할 수 있습니다.");
        }

        GroupManagerPermission perm = permissionRepository.findByGroupIdxAndUserIdx(groupIdx, targetUserIdx)
                .orElseGet(() -> GroupManagerPermission.builder()
                        .groupIdx(groupIdx)
                        .userIdx(targetUserIdx)
                        .build());
        perm.update(canManageBoard, canManageMembers, canManageRace, canManageMeetup);
        return permissionRepository.save(perm);
    }

    public GroupManagerPermission getPermission(Long groupIdx, Long ownerIdx, Long targetUserIdx) {
        groupService.validateOwner(groupIdx, ownerIdx);
        return permissionRepository.findByGroupIdxAndUserIdx(groupIdx, targetUserIdx)
                .orElseThrow(() -> new IllegalArgumentException("권한 정보가 없습니다."));
    }
}
