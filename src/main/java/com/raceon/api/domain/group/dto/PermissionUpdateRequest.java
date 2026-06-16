package com.raceon.api.domain.group.dto;

import lombok.Getter;

@Getter
public class PermissionUpdateRequest {
    private String canManageBoard;
    private String canManageMembers;
    private String canManageRace;
    private String canManageMeetup;
}
