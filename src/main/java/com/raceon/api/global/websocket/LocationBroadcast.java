package com.raceon.api.global.websocket;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LocationBroadcast {
    private final Long userIdx;
    private final Double latitude;
    private final Double longitude;
    private final Double accuracy;
    private final Long timestamp;
}
