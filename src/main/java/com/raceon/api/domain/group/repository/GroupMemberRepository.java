package com.raceon.api.domain.group.repository;

import com.raceon.api.domain.group.entity.GroupMember;
import com.raceon.api.domain.group.enums.GroupRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
    Optional<GroupMember> findByGroupIdxAndUserIdxAndDelAt(Long groupIdx, Long userIdx, String delAt);
    List<GroupMember> findByGroupIdxAndDelAt(Long groupIdx, String delAt);
    List<GroupMember> findByUserIdxAndDelAt(Long userIdx, String delAt);
    long countByGroupIdxAndDelAt(Long groupIdx, String delAt);
    boolean existsByGroupIdxAndUserIdxAndDelAt(Long groupIdx, Long userIdx, String delAt);
    List<GroupMember> findByGroupIdxAndRoleAndDelAt(Long groupIdx, GroupRole role, String delAt);
}
