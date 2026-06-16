package com.raceon.api.domain.group.service;

import com.raceon.api.domain.group.entity.Group;
import com.raceon.api.domain.group.entity.GroupMember;
import com.raceon.api.domain.group.enums.GroupRole;
import com.raceon.api.domain.group.repository.GroupMemberRepository;
import com.raceon.api.domain.group.repository.GroupRepository;
import com.raceon.api.global.upload.FileUploadService;
import com.raceon.api.global.upload.Image;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GroupService {

    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final FileUploadService fileUploadService;

    @Transactional
    public Group create(Long userIdx, String name, String description, MultipartFile imageFile,
                        Integer groupMembers, Integer managerMembers, String areaCode,
                        String tag1, String tag2, String tag3, String tag4, String tag5) {
        Group group = Group.builder()
                .ownerIdx(userIdx)
                .name(name)
                .description(description)
                .groupMembers(groupMembers)
                .managerMembers(managerMembers)
                .areaCode(areaCode)
                .tag1(tag1).tag2(tag2).tag3(tag3).tag4(tag4).tag5(tag5)
                .build();
        groupRepository.save(group);

        if (imageFile != null && !imageFile.isEmpty()) {
            Image image = fileUploadService.uploadGroupImage(imageFile, group.getGroupIdx());
            group.updateProfileImage(image);
        }

        GroupMember owner = GroupMember.builder()
                .groupIdx(group.getGroupIdx())
                .userIdx(userIdx)
                .role(GroupRole.OWNER)
                .build();
        groupMemberRepository.save(owner);
        return group;
    }

    public Group getGroup(Long groupIdx) {
        return groupRepository.findByGroupIdxAndDelAt(groupIdx, "N")
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 모임입니다."));
    }

    /** 전체 모임 탐색 (keyword·areaCode 선택 필터) */
    public List<Group> searchGroups(String keyword, String areaCode) {
        return groupRepository.search(areaCode, keyword);
    }

    /** 내 모임 멤버십 목록 (role 포함) */
    public List<GroupMember> getMyMemberships(Long userIdx) {
        return groupMemberRepository.findByUserIdxAndDelAt(userIdx, "N");
    }

    /** 모임 멤버 수 */
    public long getMemberCount(Long groupIdx) {
        return groupMemberRepository.countByGroupIdxAndDelAt(groupIdx, "N");
    }

    /** 특정 유저의 모임 내 역할 (비회원이면 null) */
    public GroupRole getUserRole(Long groupIdx, Long userIdx) {
        return groupMemberRepository.findByGroupIdxAndUserIdxAndDelAt(groupIdx, userIdx, "N")
                .map(GroupMember::getRole)
                .orElse(null);
    }

    @Transactional
    public void updateProfileImage(Long userIdx, Long groupIdx, MultipartFile imageFile) {
        Group group = getGroup(groupIdx);
        validateOwner(groupIdx, userIdx);
        Image image = fileUploadService.uploadGroupImage(imageFile, groupIdx);
        group.updateProfileImage(image);
    }

    @Transactional
    public void update(Long userIdx, Long groupIdx, String name, String description,
                       Integer groupMembers, Integer managerMembers, String areaCode,
                       String tag1, String tag2, String tag3, String tag4, String tag5) {
        Group group = getGroup(groupIdx);
        validateOwner(groupIdx, userIdx);
        group.update(name, description, null, groupMembers, managerMembers, areaCode,
                tag1, tag2, tag3, tag4, tag5);
    }

    @Transactional
    public void delete(Long userIdx, Long groupIdx) {
        Group group = getGroup(groupIdx);
        validateOwner(groupIdx, userIdx);
        group.delete();
    }

    public void validateMember(Long groupIdx, Long userIdx) {
        if (!groupMemberRepository.existsByGroupIdxAndUserIdxAndDelAt(groupIdx, userIdx, "N")) {
            throw new IllegalArgumentException("모임 멤버가 아닙니다.");
        }
    }

    public void validateOwner(Long groupIdx, Long userIdx) {
        GroupMember member = groupMemberRepository.findByGroupIdxAndUserIdxAndDelAt(groupIdx, userIdx, "N")
                .orElseThrow(() -> new IllegalArgumentException("모임 멤버가 아닙니다."));
        if (member.getRole() != GroupRole.OWNER) {
            throw new IllegalArgumentException("모임장만 가능한 작업입니다.");
        }
    }

    public void validateOwnerOrManager(Long groupIdx, Long userIdx) {
        GroupMember member = groupMemberRepository.findByGroupIdxAndUserIdxAndDelAt(groupIdx, userIdx, "N")
                .orElseThrow(() -> new IllegalArgumentException("모임 멤버가 아닙니다."));
        if (member.getRole() == GroupRole.MEMBER) {
            throw new IllegalArgumentException("모임장 또는 운영진만 가능한 작업입니다.");
        }
    }
}
