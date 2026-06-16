package com.raceon.api.domain.area.dto;

import com.raceon.api.domain.area.entity.Area;
import lombok.Getter;

@Getter
public class AreaResponse {
    private final Long   areaIdx;
    private final String areaCode;
    private final String areaName;
    private final int    areaLevel;
    private final String parentCode;
    private final String fullName;

    public AreaResponse(Area area) {
        this.areaIdx   = area.getAreaIdx();
        this.areaCode  = area.getAreaCode();
        this.areaName  = area.getAreaName();
        this.areaLevel = area.getAreaLevel();
        this.parentCode = area.getParentCode();
        this.fullName  = area.getFullName();
    }
}
