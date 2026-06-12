package com.raceon.api.domain.userrace.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserRaceRecordUpdateRequest {
    private String course;
    private String bibNumber;
    private String recordTime;
    private String pace;
    private Integer ranking;
    private String finishYn;
    private String memo;
}
