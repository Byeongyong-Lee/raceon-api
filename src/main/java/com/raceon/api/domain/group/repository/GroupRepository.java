package com.raceon.api.domain.group.repository;

import com.raceon.api.domain.group.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group, Long> {
    Optional<Group> findByGroupIdxAndDelAt(Long groupIdx, String delAt);

    @Query("SELECT g FROM Group g WHERE g.delAt = 'N' " +
           "AND (:areaCode IS NULL OR g.areaCode = :areaCode) " +
           "AND (:keyword IS NULL OR g.name LIKE %:keyword% OR g.description LIKE %:keyword% " +
           "     OR g.tag1 LIKE %:keyword% OR g.tag2 LIKE %:keyword% OR g.tag3 LIKE %:keyword% " +
           "     OR g.tag4 LIKE %:keyword% OR g.tag5 LIKE %:keyword%)")
    List<Group> search(@Param("areaCode") String areaCode, @Param("keyword") String keyword);
}
