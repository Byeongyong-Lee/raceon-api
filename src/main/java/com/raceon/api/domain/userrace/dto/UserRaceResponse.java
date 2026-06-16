package com.raceon.api.domain.userrace.dto;

import com.raceon.api.domain.userrace.entity.UserRace;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class UserRaceResponse {
    private final Long userRaceIdx;

    // 대회 정보
    private final Long raceIdx;
    private final String raceName;
    private final LocalDate raceDate;
    private final String raceLocation;

    // 참가 정보
    private final String course;
    private final String bibNumber;

    // 기록
    private final String recordTime;
    private final String pace;
    private final Integer ranking;
    private final String finishYn;

    // 메모
    private final String memo;

    // 기록증 이미지
    private final String recordImagePath;

    private final String delAt;
    private final LocalDateTime createDt;
    private final LocalDateTime updateDt;

    public UserRaceResponse(UserRace userRace) {
        this.userRaceIdx = userRace.getUserRaceIdx();
        this.raceIdx = userRace.getRace().getRaceIdx();
        this.raceName = userRace.getRace().getName();
        this.raceDate = userRace.getRace().getRaceDate();
        this.raceLocation = userRace.getRace().getLocation();
        this.course = userRace.getCourse();
        this.bibNumber = userRace.getBibNumber();
        this.recordTime = userRace.getRecordTime();
        this.pace = userRace.getPace();
        this.ranking = userRace.getRanking();
        this.finishYn = userRace.getFinishYn();
        this.memo = userRace.getMemo();
        this.recordImagePath = userRace.getRecordImage() != null ? userRace.getRecordImage().getFilePath() : null;
        this.delAt = userRace.getDelAt();
        this.createDt = userRace.getCreateDt();
        this.updateDt = userRace.getUpdateDt();
    }
}
