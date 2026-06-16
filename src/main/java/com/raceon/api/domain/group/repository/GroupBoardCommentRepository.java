package com.raceon.api.domain.group.repository;

import com.raceon.api.domain.group.entity.GroupBoardComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GroupBoardCommentRepository extends JpaRepository<GroupBoardComment, Long> {
    List<GroupBoardComment> findByBoardIdxAndDelAt(Long boardIdx, String delAt);
    Optional<GroupBoardComment> findByCommentIdxAndDelAt(Long commentIdx, String delAt);
}
