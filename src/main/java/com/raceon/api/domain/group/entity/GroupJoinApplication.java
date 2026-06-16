package com.raceon.api.domain.group.entity;

import com.raceon.api.domain.group.enums.ApplicationStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "group_join_application",
        uniqueConstraints = @UniqueConstraint(columnNames = {"group_idx", "user_idx"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class GroupJoinApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "application_idx")
    private Long applicationIdx;

    @Column(name = "group_idx", nullable = false)
    private Long groupIdx;

    @Column(name = "user_idx", nullable = false)
    private Long userIdx;

    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10)
    @Builder.Default
    private ApplicationStatus status = ApplicationStatus.PENDING;

    @Column(name = "processed_by")
    private Long processedBy;

    @CreationTimestamp
    @Column(name = "create_dt", nullable = false, updatable = false)
    private LocalDateTime createDt;

    @UpdateTimestamp
    @Column(name = "update_dt", nullable = false)
    private LocalDateTime updateDt;

    public void approve(Long processedBy) {
        this.status = ApplicationStatus.APPROVED;
        this.processedBy = processedBy;
    }

    public void reject(Long processedBy) {
        this.status = ApplicationStatus.REJECTED;
        this.processedBy = processedBy;
    }
}
