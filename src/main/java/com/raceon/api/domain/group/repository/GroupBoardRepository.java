package com.raceon.api.domain.group.repository;

import com.raceon.api.domain.group.entity.GroupBoard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GroupBoardRepository extends JpaRepository<GroupBoard, Long> {
    Page<GroupBoard> findByGroupIdxAndDelAt(Long groupIdx, String delAt, Pageable pageable);
    List<GroupBoard> findByGroupIdxAndIsNoticeAndDelAt(Long groupIdx, String isNotice, String delAt);
    Optional<GroupBoard> findByBoardIdxAndDelAt(Long boardIdx, String delAt);
}
