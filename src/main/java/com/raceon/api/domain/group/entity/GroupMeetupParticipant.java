package com.raceon.api.domain.group.entity;

import com.raceon.api.domain.group.enums.MeetupStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "group_meetup_participant",
        uniqueConstraints = @UniqueConstraint(columnNames = {"meetup_idx", "user_idx"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class GroupMeetupParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "participant_idx")
    private Long participantIdx;

    @Column(name = "meetup_idx", nullable = false)
    private Long meetupIdx;

    @Column(name = "user_idx", nullable = false)
    private Long userIdx;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10)
    @Builder.Default
    private MeetupStatus status = MeetupStatus.PENDING;

    @CreationTimestamp
    @Column(name = "create_dt", nullable = false, updatable = false)
    private LocalDateTime createDt;

    @UpdateTimestamp
    @Column(name = "update_dt", nullable = false)
    private LocalDateTime updateDt;

    public void updateStatus(MeetupStatus status) {
        this.status = status;
    }
}
