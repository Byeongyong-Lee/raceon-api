package com.raceon.api.domain.race.controller;

import com.raceon.api.domain.race.dto.RaceResponse;
import com.raceon.api.domain.race.service.RaceService;
import com.raceon.api.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class RaceController {

    private final RaceService raceService;

    @GetMapping("/api/races")
    public ApiResponse<List<RaceResponse>> getRaces(
            @RequestParam(required = false) String month) {
        return ApiResponse.ok(raceService.getRaces(month));
    }
}
