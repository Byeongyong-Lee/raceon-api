package com.raceon.api.domain.group.dto;

import lombok.Getter;

@Getter
public class GroupUpdateRequest {
    private String name;
    private String description;
    private String profileImage;
    private Integer groupMembers;
    private Integer managerMembers;
    private String areaCode;
    private String tag1;
    private String tag2;
    private String tag3;
    private String tag4;
    private String tag5;
}
