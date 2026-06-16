package com.raceon.api.domain.group.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "group_race",
        uniqueConstraints = @UniqueConstraint(columnNames = {"group_idx", "race_idx"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class GroupRace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_race_idx")
    private Long groupRaceIdx;

    @Column(name = "group_idx", nullable = false)
    private Long groupIdx;

    @Column(name = "race_idx", nullable = false)
    private Long raceIdx;

    @Column(name = "linked_by", nullable = false)
    private Long linkedBy;

    @Column(name = "location_share_start")
    private LocalDateTime locationShareStart;

    @Column(name = "location_share_end")
    private LocalDateTime locationShareEnd;

    @CreationTimestamp
    @Column(name = "create_dt", nullable = false, updatable = false)
    private LocalDateTime createDt;

    public void updateLocationShareTime(LocalDateTime start, LocalDateTime end) {
        this.locationShareStart = start;
        this.locationShareEnd = end;
    }
}
