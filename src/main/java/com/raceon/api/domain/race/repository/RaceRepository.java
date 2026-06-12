package com.raceon.api.domain.race.repository;

import com.raceon.api.domain.race.entity.Race;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RaceRepository extends JpaRepository<Race, Long>, RaceRepositoryCustom {
}
