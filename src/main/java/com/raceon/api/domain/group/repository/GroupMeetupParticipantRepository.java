package com.raceon.api.domain.group.repository;

import com.raceon.api.domain.group.entity.GroupMeetupParticipant;
import com.raceon.api.domain.group.enums.MeetupStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GroupMeetupParticipantRepository extends JpaRepository<GroupMeetupParticipant, Long> {
    Optional<GroupMeetupParticipant> findByMeetupIdxAndUserIdx(Long meetupIdx, Long userIdx);
    List<GroupMeetupParticipant> findByMeetupIdx(Long meetupIdx);
    long countByMeetupIdxAndStatus(Long meetupIdx, MeetupStatus status);
}
