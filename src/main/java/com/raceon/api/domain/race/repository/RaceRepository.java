package com.raceon.api.domain.race.repository;

import com.raceon.api.domain.race.entity.Race;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RaceRepository extends JpaRepository<Race, Long> {
    Optional<Race> findBySourceId(String sourceId);
    List<Race> findAllByOrderByRaceDateAsc();
    List<Race> findByRaceDateBetweenOrderByRaceDateAsc(LocalDate start, LocalDate end);
}
