package com.raceon.api.domain.group.dto;

import com.raceon.api.domain.group.entity.Group;
import com.raceon.api.domain.group.enums.GroupRole;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class GroupResponse {
    private final Long groupIdx;
    private final String name;
    private final String description;
    private final Integer groupMembers;
    private final Integer managerMembers;
    private final String areaCode;
    private final String tag1;
    private final String tag2;
    private final String tag3;
    private final String tag4;
    private final String tag5;
    private final String profileImage;
    private final Long ownerIdx;
    private final LocalDateTime createDt;
    private final GroupRole role;       // 비회원/비인증 시 null
    private final long memberCount;

    public GroupResponse(Group group, GroupRole role, long memberCount) {
        this.groupIdx = group.getGroupIdx();
        this.name = group.getName();
        this.description = group.getDescription();
        this.groupMembers = group.getGroupMembers();
        this.managerMembers = group.getManagerMembers();
        this.areaCode = group.getAreaCode();
        this.tag1 = group.getTag1();
        this.tag2 = group.getTag2();
        this.tag3 = group.getTag3();
        this.tag4 = group.getTag4();
        this.tag5 = group.getTag5();
        this.profileImage = group.getProfileImage() != null ? group.getProfileImage().getFilePath() : null;
        this.ownerIdx = group.getOwnerIdx();
        this.createDt = group.getCreateDt();
        this.role = role;
        this.memberCount = memberCount;
    }
}
