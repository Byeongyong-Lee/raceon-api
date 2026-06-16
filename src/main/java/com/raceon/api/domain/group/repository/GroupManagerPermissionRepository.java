package com.raceon.api.domain.group.repository;

import com.raceon.api.domain.group.entity.GroupManagerPermission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GroupManagerPermissionRepository extends JpaRepository<GroupManagerPermission, Long> {
    Optional<GroupManagerPermission> findByGroupIdxAndUserIdx(Long groupIdx, Long userIdx);
    void deleteByGroupIdxAndUserIdx(Long groupIdx, Long userIdx);
}
