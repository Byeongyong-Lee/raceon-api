package com.raceon.api.global.websocket;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LocationMessage {
    private Long groupRaceIdx;
    private Double latitude;
    private Double longitude;
    private Double accuracy;
    private Long timestamp;
}
