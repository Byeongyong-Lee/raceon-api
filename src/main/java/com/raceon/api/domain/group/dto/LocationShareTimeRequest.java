package com.raceon.api.domain.group.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class LocationShareTimeRequest {
    private LocalDateTime locationShareStart;
    private LocalDateTime locationShareEnd;
}
