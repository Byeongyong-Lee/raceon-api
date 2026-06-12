package com.raceon.api.domain.userrace.entity;

import com.raceon.api.domain.auth.entity.User;
import com.raceon.api.domain.race.entity.Race;
import com.raceon.api.domain.userrace.dto.UserRaceRecordUpdateRequest;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_race")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserRace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_race_idx")
    private Long userRaceIdx;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_idx", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "race_idx", nullable = false)
    private Race race;

    @Column(length = 20)
    private String course;

    @Column(name = "bib_number", length = 20)
    private String bibNumber;

    @Column(name = "record_time", length = 10)
    private String recordTime;

    @Column(length = 10)
    private String pace;

    private Integer ranking;

    @Column(name = "finish_yn", length = 1)
    private String finishYn;

    @Column(columnDefinition = "TEXT")
    private String memo;

    @Column(name = "del_at", nullable = false, length = 1)
    @Builder.Default
    private String delAt = "N";

    @CreationTimestamp
    @Column(name = "create_dt", updatable = false, nullable = false)
    private LocalDateTime createDt;

    @UpdateTimestamp
    @Column(name = "update_dt", nullable = false)
    private LocalDateTime updateDt;

    public void cancel() {
        this.delAt = "Y";
    }

    public void updateRecord(UserRaceRecordUpdateRequest request) {
        this.course = request.getCourse();
        this.bibNumber = request.getBibNumber();
        this.recordTime = request.getRecordTime();
        this.pace = request.getPace();
        this.ranking = request.getRanking();
        this.finishYn = request.getFinishYn();
        this.memo = request.getMemo();
    }
}
