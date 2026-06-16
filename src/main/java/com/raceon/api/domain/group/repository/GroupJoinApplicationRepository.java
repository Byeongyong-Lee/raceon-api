package com.raceon.api.domain.group.repository;

import com.raceon.api.domain.group.entity.GroupJoinApplication;
import com.raceon.api.domain.group.enums.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GroupJoinApplicationRepository extends JpaRepository<GroupJoinApplication, Long> {
    Optional<GroupJoinApplication> findByGroupIdxAndUserIdx(Long groupIdx, Long userIdx);
    List<GroupJoinApplication> findByGroupIdxAndStatusOrderByCreateDtDesc(Long groupIdx, ApplicationStatus status);
    List<GroupJoinApplication> findByGroupIdxOrderByCreateDtDesc(Long groupIdx);
    Optional<GroupJoinApplication> findByApplicationIdxAndGroupIdx(Long applicationIdx, Long groupIdx);
}
