package com.raceon.api.domain.race.repository;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class RaceSearchCondition {
    private String sourceId;
    private LocalDate raceDateFrom;
    private LocalDate raceDateTo;
}
