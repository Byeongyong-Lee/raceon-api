package com.raceon.api.domain.group.repository;

import com.raceon.api.domain.group.entity.GroupRace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GroupRaceRepository extends JpaRepository<GroupRace, Long> {
    List<GroupRace> findByGroupIdx(Long groupIdx);
    Optional<GroupRace> findByGroupIdxAndRaceIdx(Long groupIdx, Long raceIdx);
    boolean existsByGroupIdxAndRaceIdx(Long groupIdx, Long raceIdx);
}
