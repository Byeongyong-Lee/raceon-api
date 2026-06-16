package com.raceon.api.domain.group.repository;

import com.raceon.api.domain.group.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group, Long> {
    Optional<Group> findByGroupIdxAndDelAt(Long groupIdx, String delAt);
}
