package com.raceon.api.domain.userrace.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserRaceRegisterRequest {
    private Long raceIdx;
    private String course;
}
