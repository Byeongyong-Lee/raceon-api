package com.raceon.api.domain.group.dto;

import com.raceon.api.domain.group.entity.GroupMeetup;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class MeetupResponse {
    private final Long meetupIdx;
    private final Long groupIdx;
    private final Long createdBy;
    private final String title;
    private final String description;
    private final LocalDateTime meetupDt;
    private final String location;
    private final LocalDateTime createDt;

    public MeetupResponse(GroupMeetup meetup) {
        this.meetupIdx = meetup.getMeetupIdx();
        this.groupIdx = meetup.getGroupIdx();
        this.createdBy = meetup.getCreatedBy();
        this.title = meetup.getTitle();
        this.description = meetup.getDescription();
        this.meetupDt = meetup.getMeetupDt();
        this.location = meetup.getLocation();
        this.createDt = meetup.getCreateDt();
    }
}
