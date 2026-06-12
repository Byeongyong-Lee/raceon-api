package com.raceon.api.domain.userrace.repository;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserRaceSearchCondition {
    private Long userIdx;
    private Long raceIdx;
    private String delAt;
    private String course;
    private String finishYn;
}
