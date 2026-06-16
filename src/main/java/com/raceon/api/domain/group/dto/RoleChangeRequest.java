package com.raceon.api.domain.group.dto;

import com.raceon.api.domain.group.enums.GroupRole;
import lombok.Getter;

@Getter
public class RoleChangeRequest {
    private GroupRole role;
}
