package com.raceon.api.domain.userrace.repository;

import com.raceon.api.domain.userrace.entity.UserRace;

import java.util.List;

public interface UserRaceRepositoryCustom {
    List<UserRace> search(UserRaceSearchCondition condition);
    boolean existsActive(UserRaceSearchCondition condition);
}
