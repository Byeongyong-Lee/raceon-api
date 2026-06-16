package com.raceon.api.domain.group.repository;

import com.raceon.api.domain.group.entity.GroupMeetup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface GroupMeetupRepository extends JpaRepository<GroupMeetup, Long> {
    @Query("SELECT m FROM GroupMeetup m WHERE m.groupIdx = :groupIdx AND m.delAt = 'N' AND m.meetupDt >= :now ORDER BY m.meetupDt ASC")
    List<GroupMeetup> findUpcomingByGroupIdx(@Param("groupIdx") Long groupIdx, @Param("now") LocalDateTime now);

    Optional<GroupMeetup> findByMeetupIdxAndDelAt(Long meetupIdx, String delAt);
}
