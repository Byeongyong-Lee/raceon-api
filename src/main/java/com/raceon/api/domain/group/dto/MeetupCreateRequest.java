package com.raceon.api.domain.group.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class MeetupCreateRequest {
    private String title;
    private String description;
    private LocalDateTime meetupDt;
    private String location;
}
