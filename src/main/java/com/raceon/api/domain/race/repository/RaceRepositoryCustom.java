package com.raceon.api.domain.race.repository;

import com.raceon.api.domain.race.entity.Race;

import java.util.List;
import java.util.Optional;

public interface RaceRepositoryCustom {
    Optional<Race> findOne(RaceSearchCondition condition);
    List<Race> search(RaceSearchCondition condition);
}
