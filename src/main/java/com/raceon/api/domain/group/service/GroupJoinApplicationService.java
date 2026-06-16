package com.raceon.api.domain.group.service;

import com.raceon.api.domain.group.entity.GroupJoinApplication;
import com.raceon.api.domain.group.entity.GroupManagerPermission;
import com.raceon.api.domain.group.entity.GroupMember;
import com.raceon.api.domain.group.enums.ApplicationStatus;
import com.raceon.api.domain.group.enums.GroupRole;
import com.raceon.api.domain.group.repository.GroupJoinApplicationRepository;
import com.raceon.api.domain.group.repository.GroupManagerPermissionRepository;
import com.raceon.api.domain.group.repository.GroupMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GroupJoinApplicationService {

    private final GroupJoinApplicationRepository applicationRepository;
    private final GroupMemberRepository memberRepository;
    private final GroupManagerPermissionRepository permissionRepository;
    private final GroupService groupService;

    @Transactional
    public GroupJoinApplication apply(Long groupIdx, Long userIdx, String message) {
        groupService.getGroup(groupIdx);

        if (memberRepository.existsByGroupIdxAndUserIdxAndDelAt(groupIdx, userIdx, "N")) {
            throw new IllegalArgumentException("이미 가입된 모임입니다.");
        }

        applicationRepository.findByGroupIdxAndUserIdx(groupIdx, userIdx).ifPresent(existing -> {
            if (existing.getStatus() == ApplicationStatus.PENDING) {
                throw new IllegalArgumentException("이미 가입 신청 중입니다.");
            }
            if (existing.getStatus() == ApplicationStatus.APPROVED) {
                throw new IllegalArgumentException("이미 승인된 신청이 있습니다.");
            }
        });

        long currentCount = memberRepository.countByGroupIdxAndDelAt(groupIdx, "N");
        Integer groupMembers = groupService.getGroup(groupIdx).getGroupMembers();
        if (groupMembers != null && currentCount >= groupMembers) {
            throw new IllegalArgumentException("모임 최대 인원을 초과했습니다.");
        }

        GroupJoinApplication application = GroupJoinApplication.builder()
                .groupIdx(groupIdx)
                .userIdx(userIdx)
                .message(message)
                .build();
        return applicationRepository.save(application);
    }

    public List<GroupJoinApplication> getApplications(Long groupIdx, Long requestUserIdx, ApplicationStatus status) {
        validateManagerPermission(groupIdx, requestUserIdx);
        if (status != null) {
            return applicationRepository.findByGroupIdxAndStatusOrderByCreateDtDesc(groupIdx, status);
        }
        return applicationRepository.findByGroupIdxOrderByCreateDtDesc(groupIdx);
    }

    public GroupJoinApplication getMyApplication(Long groupIdx, Long userIdx) {
        return applicationRepository.findByGroupIdxAndUserIdx(groupIdx, userIdx)
                .orElseThrow(() -> new IllegalArgumentException("신청 내역이 없습니다."));
    }

    @Transactional
    public void approve(Long groupIdx, Long requestUserIdx, Long applicationIdx) {
        validateManagerPermission(groupIdx, requestUserIdx);

        GroupJoinApplication application = applicationRepository
                .findByApplicationIdxAndGroupIdx(applicationIdx, groupIdx)
                .orElseThrow(() -> new IllegalArgumentException("신청 내역을 찾을 수 없습니다."));

        if (application.getStatus() != ApplicationStatus.PENDING) {
            throw new IllegalArgumentException("대기 중인 신청만 처리할 수 있습니다.");
        }

        long currentCount = memberRepository.countByGroupIdxAndDelAt(groupIdx, "N");
        Integer groupMembers = groupService.getGroup(groupIdx).getGroupMembers();
        if (groupMembers != null && currentCount >= groupMembers) {
            throw new IllegalArgumentException("모임 최대 인원을 초과했습니다.");
        }

        application.approve(requestUserIdx);

        GroupMember newMember = GroupMember.builder()
                .groupIdx(groupIdx)
                .userIdx(application.getUserIdx())
                .build();
        memberRepository.save(newMember);
    }

    @Transactional
    public void reject(Long groupIdx, Long requestUserIdx, Long applicationIdx) {
        validateManagerPermission(groupIdx, requestUserIdx);

        GroupJoinApplication application = applicationRepository
                .findByApplicationIdxAndGroupIdx(applicationIdx, groupIdx)
                .orElseThrow(() -> new IllegalArgumentException("신청 내역을 찾을 수 없습니다."));

        if (application.getStatus() != ApplicationStatus.PENDING) {
            throw new IllegalArgumentException("대기 중인 신청만 처리할 수 있습니다.");
        }

        application.reject(requestUserIdx);
    }

    private void validateManagerPermission(Long groupIdx, Long userIdx) {
        GroupMember member = memberRepository.findByGroupIdxAndUserIdxAndDelAt(groupIdx, userIdx, "N")
                .orElseThrow(() -> new IllegalArgumentException("모임 멤버가 아닙니다."));
        if (member.getRole() == GroupRole.OWNER) return;
        if (member.getRole() == GroupRole.MANAGER) {
            GroupManagerPermission perm = permissionRepository.findByGroupIdxAndUserIdx(groupIdx, userIdx)
                    .orElseThrow(() -> new IllegalArgumentException("권한 정보가 없습니다."));
            if ("Y".equals(perm.getCanManageMembers())) return;
        }
        throw new IllegalArgumentException("가입 신청 관리 권한이 없습니다.");
    }
}
