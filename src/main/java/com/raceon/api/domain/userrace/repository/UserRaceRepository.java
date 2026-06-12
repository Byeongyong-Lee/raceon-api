package com.raceon.api.domain.userrace.repository;

import com.raceon.api.domain.userrace.entity.UserRace;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRaceRepository extends JpaRepository<UserRace, Long>, UserRaceRepositoryCustom {
}
