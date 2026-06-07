package com.raceon.api.domain.race.controller;

import com.raceon.api.domain.race.service.RaceCrawlerService;
import com.raceon.api.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
public class RaceAdminController {

    private final RaceCrawlerService raceCrawlerService;

    @PostMapping("/api/admin/crawl")
    public ApiResponse<Integer> crawl(@RequestParam(defaultValue = "0") int year) {
        int targetYear = year > 0 ? year : LocalDate.now().getYear();
        try {
            return ApiResponse.ok(raceCrawlerService.crawl(targetYear));
        } catch (IOException e) {
            throw new IllegalStateException("크롤링 실패: " + e.getMessage());
        }
    }
}
