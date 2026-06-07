package com.raceon.api.domain.race.dto;

import com.raceon.api.domain.race.entity.Race;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class RaceResponse {
    private final Long id;
    private final String sourceId;
    private final String name;
    private final LocalDate raceDate;
    private final String location;
    private final String course;
    private final String organizer;
    private final String phone;
    private final String homepage;
    private final String detailUrl;

    public RaceResponse(Race race) {
        this.id = race.getRaceIdx();
        this.sourceId = race.getSourceId();
        this.name = race.getName();
        this.raceDate = race.getRaceDate();
        this.location = race.getLocation();
        this.course = race.getCourse();
        this.organizer = race.getOrganizer();
        this.phone = race.getPhone();
        this.homepage = race.getHomepage();
        this.detailUrl = race.getDetailUrl();
    }
}
