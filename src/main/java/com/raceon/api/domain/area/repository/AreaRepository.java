package com.raceon.api.domain.area.repository;

import com.raceon.api.domain.area.entity.Area;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AreaRepository extends JpaRepository<Area, Long> {
    List<Area> findAllByOrderByAreaCode();
    List<Area> findByAreaLevelOrderByAreaCode(int areaLevel);
    List<Area> findByAreaLevelAndParentCodeOrderByAreaCode(int areaLevel, String parentCode);
}
