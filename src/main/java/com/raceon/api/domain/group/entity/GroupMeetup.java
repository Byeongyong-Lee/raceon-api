package com.raceon.api.domain.group.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "group_meetup")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class GroupMeetup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "meetup_idx")
    private Long meetupIdx;

    @Column(name = "group_idx", nullable = false)
    private Long groupIdx;

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "meetup_dt", nullable = false)
    private LocalDateTime meetupDt;

    @Column(name = "location", length = 200)
    private String location;

    @Column(name = "del_at", nullable = false, length = 1)
    @Builder.Default
    private String delAt = "N";

    @CreationTimestamp
    @Column(name = "create_dt", nullable = false, updatable = false)
    private LocalDateTime createDt;

    @UpdateTimestamp
    @Column(name = "update_dt", nullable = false)
    private LocalDateTime updateDt;

    public void update(String title, String description, LocalDateTime meetupDt, String location) {
        if (title != null) this.title = title;
        if (description != null) this.description = description;
        if (meetupDt != null) this.meetupDt = meetupDt;
        if (location != null) this.location = location;
    }

    public void delete() {
        this.delAt = "Y";
    }
}
