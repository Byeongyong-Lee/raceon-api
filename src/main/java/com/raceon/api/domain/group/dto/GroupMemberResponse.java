package com.raceon.api.domain.group.dto;

import com.raceon.api.domain.group.entity.GroupMember;
import com.raceon.api.domain.group.enums.GroupRole;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class GroupMemberResponse {
    private final Long groupMemberIdx;
    private final Long userIdx;
    private final GroupRole role;
    private final LocalDateTime createDt;

    public GroupMemberResponse(GroupMember member) {
        this.groupMemberIdx = member.getGroupMemberIdx();
        this.userIdx = member.getUserIdx();
        this.role = member.getRole();
        this.createDt = member.getCreateDt();
    }
}
