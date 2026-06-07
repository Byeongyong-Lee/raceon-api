package com.raceon.api.domain.race.service;

import com.raceon.api.domain.race.dto.RaceResponse;
import com.raceon.api.domain.race.repository.RaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RaceService {

    private final RaceRepository raceRepository;

    public List<RaceResponse> getRaces(String yyyymm) {
        if (yyyymm == null || yyyymm.isBlank()) {
            return raceRepository.findAllByOrderByRaceDateAsc()
                    .stream().map(RaceResponse::new).toList();
        }
        if (!yyyymm.matches("\\d{6}")) {
            throw new IllegalArgumentException("month 형식이 올바르지 않습니다. (예: 202601)");
        }
        int year = Integer.parseInt(yyyymm.substring(0, 4));
        int month = Integer.parseInt(yyyymm.substring(4, 6));
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        return raceRepository.findByRaceDateBetweenOrderByRaceDateAsc(start, end)
                .stream().map(RaceResponse::new).toList();
    }
}
