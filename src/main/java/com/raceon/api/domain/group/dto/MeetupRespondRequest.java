package com.raceon.api.domain.group.dto;

import com.raceon.api.domain.group.enums.MeetupStatus;
import lombok.Getter;

@Getter
public class MeetupRespondRequest {
    private MeetupStatus status;
}
