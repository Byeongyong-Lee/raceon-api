package com.raceon.api.domain.group.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "group_manager_permission",
        uniqueConstraints = @UniqueConstraint(columnNames = {"group_idx", "user_idx"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class GroupManagerPermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "permission_idx")
    private Long permissionIdx;

    @Column(name = "group_idx", nullable = false)
    private Long groupIdx;

    @Column(name = "user_idx", nullable = false)
    private Long userIdx;

    @Column(name = "can_manage_board", nullable = false, length = 1)
    @Builder.Default
    private String canManageBoard = "N";

    @Column(name = "can_manage_members", nullable = false, length = 1)
    @Builder.Default
    private String canManageMembers = "N";

    @Column(name = "can_manage_race", nullable = false, length = 1)
    @Builder.Default
    private String canManageRace = "N";

    @Column(name = "can_manage_meetup", nullable = false, length = 1)
    @Builder.Default
    private String canManageMeetup = "N";

    @CreationTimestamp
    @Column(name = "create_dt", nullable = false, updatable = false)
    private LocalDateTime createDt;

    @UpdateTimestamp
    @Column(name = "update_dt", nullable = false)
    private LocalDateTime updateDt;

    public void update(String canManageBoard, String canManageMembers, String canManageRace, String canManageMeetup) {
        this.canManageBoard = canManageBoard;
        this.canManageMembers = canManageMembers;
        this.canManageRace = canManageRace;
        this.canManageMeetup = canManageMeetup;
    }
}
