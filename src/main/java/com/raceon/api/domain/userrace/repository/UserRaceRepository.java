package com.raceon.api.domain.userrace.repository;

import com.raceon.api.domain.userrace.entity.UserRace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRaceRepository extends JpaRepository<UserRace, Long> {
    List<UserRace> findByUserUserIdxAndDelAtOrderByCreateDtDesc(Long userIdx, String delAt);
    boolean existsByUserUserIdxAndRaceRaceIdxAndDelAt(Long userIdx, Long raceIdx, String delAt);
}
