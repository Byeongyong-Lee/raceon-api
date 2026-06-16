package com.raceon.api.domain.group.service;

import com.raceon.api.domain.group.entity.GroupMember;
import com.raceon.api.domain.group.entity.GroupManagerPermission;
import com.raceon.api.domain.group.entity.GroupRace;
import com.raceon.api.domain.group.enums.GroupRole;
import com.raceon.api.domain.group.repository.GroupManagerPermissionRepository;
import com.raceon.api.domain.group.repository.GroupMemberRepository;
import com.raceon.api.domain.group.repository.GroupRaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GroupRaceService {

    private final GroupRaceRepository groupRaceRepository;
    private final GroupMemberRepository memberRepository;
    private final GroupManagerPermissionRepository permissionRepository;
    private final GroupService groupService;

    public List<GroupRace> getRaces(Long groupIdx, Long userIdx) {
        groupService.validateMember(groupIdx, userIdx);
        return groupRaceRepository.findByGroupIdx(groupIdx);
    }

    @Transactional
    public GroupRace linkRace(Long groupIdx, Long userIdx, Long raceIdx) {
        validateRacePermission(groupIdx, userIdx);
        if (groupRaceRepository.existsByGroupIdxAndRaceIdx(groupIdx, raceIdx)) {
            throw new IllegalArgumentException("이미 연동된 대회입니다.");
        }
        GroupRace groupRace = GroupRace.builder()
                .groupIdx(groupIdx)
                .raceIdx(raceIdx)
                .linkedBy(userIdx)
                .build();
        return groupRaceRepository.save(groupRace);
    }

    @Transactional
    public void unlinkRace(Long groupIdx, Long userIdx, Long groupRaceIdx) {
        validateRacePermission(groupIdx, userIdx);
        GroupRace groupRace = groupRaceRepository.findById(groupRaceIdx)
                .orElseThrow(() -> new IllegalArgumentException("연동 정보를 찾을 수 없습니다."));
        if (!groupRace.getGroupIdx().equals(groupIdx)) {
            throw new IllegalArgumentException("해당 모임의 연동 대회가 아닙니다.");
        }
        groupRaceRepository.delete(groupRace);
    }

    @Transactional
    public GroupRace updateLocationShareTime(Long groupIdx, Long userIdx, Long groupRaceIdx,
                                              LocalDateTime start, LocalDateTime end) {
        validateRacePermission(groupIdx, userIdx);
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("시작 시간이 종료 시간보다 늦을 수 없습니다.");
        }
        GroupRace groupRace = groupRaceRepository.findById(groupRaceIdx)
                .orElseThrow(() -> new IllegalArgumentException("연동 정보를 찾을 수 없습니다."));
        groupRace.updateLocationShareTime(start, end);
        return groupRace;
    }

    public boolean isLocationShareActive(Long groupRaceIdx) {
        GroupRace groupRace = groupRaceRepository.findById(groupRaceIdx)
                .orElseThrow(() -> new IllegalArgumentException("연동 정보를 찾을 수 없습니다."));
        LocalDateTime now = LocalDateTime.now();
        return groupRace.getLocationShareStart() != null
                && groupRace.getLocationShareEnd() != null
                && now.isAfter(groupRace.getLocationShareStart())
                && now.isBefore(groupRace.getLocationShareEnd());
    }

    private void validateRacePermission(Long groupIdx, Long userIdx) {
        GroupMember member = memberRepository.findByGroupIdxAndUserIdxAndDelAt(groupIdx, userIdx, "N")
                .orElseThrow(() -> new IllegalArgumentException("모임 멤버가 아닙니다."));
        if (member.getRole() == GroupRole.OWNER) return;
        if (member.getRole() == GroupRole.MANAGER) {
            GroupManagerPermission perm = permissionRepository.findByGroupIdxAndUserIdx(groupIdx, userIdx)
                    .orElseThrow(() -> new IllegalArgumentException("권한 정보가 없습니다."));
            if ("Y".equals(perm.getCanManageRace())) return;
        }
        throw new IllegalArgumentException("대회 관리 권한이 없습니다.");
    }
}
