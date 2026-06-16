package com.raceon.api.domain.group.dto;

import com.raceon.api.domain.group.entity.GroupJoinApplication;
import com.raceon.api.domain.group.enums.ApplicationStatus;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ApplicationResponse {
    private final Long applicationIdx;
    private final Long groupIdx;
    private final Long userIdx;
    private final String message;
    private final ApplicationStatus status;
    private final Long processedBy;
    private final LocalDateTime createDt;
    private final LocalDateTime updateDt;

    public ApplicationResponse(GroupJoinApplication app) {
        this.applicationIdx = app.getApplicationIdx();
        this.groupIdx = app.getGroupIdx();
        this.userIdx = app.getUserIdx();
        this.message = app.getMessage();
        this.status = app.getStatus();
        this.processedBy = app.getProcessedBy();
        this.createDt = app.getCreateDt();
        this.updateDt = app.getUpdateDt();
    }
}
