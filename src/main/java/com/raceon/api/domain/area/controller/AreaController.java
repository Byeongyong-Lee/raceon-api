package com.raceon.api.domain.area.controller;

import com.raceon.api.domain.area.dto.AreaResponse;
import com.raceon.api.domain.area.service.AreaService;
import com.raceon.api.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AreaController {

    private final AreaService areaService;

    @GetMapping("/api/areas")
    public ApiResponse<List<AreaResponse>> getAreas(
            @RequestParam(required = false) Integer level,
            @RequestParam(required = false) String parentCode) {
        return ApiResponse.ok(areaService.getAreas(level, parentCode));
    }
}
