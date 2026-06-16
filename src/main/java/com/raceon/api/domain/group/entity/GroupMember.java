package com.raceon.api.domain.group.entity;

import com.raceon.api.domain.group.enums.GroupRole;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "group_member",
        uniqueConstraints = @UniqueConstraint(columnNames = {"group_idx", "user_idx"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class GroupMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_member_idx")
    private Long groupMemberIdx;

    @Column(name = "group_idx", nullable = false)
    private Long groupIdx;

    @Column(name = "user_idx", nullable = false)
    private Long userIdx;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 10)
    @Builder.Default
    private GroupRole role = GroupRole.MEMBER;

    @Column(name = "del_at", nullable = false, length = 1)
    @Builder.Default
    private String delAt = "N";

    @CreationTimestamp
    @Column(name = "create_dt", nullable = false, updatable = false)
    private LocalDateTime createDt;

    @UpdateTimestamp
    @Column(name = "update_dt", nullable = false)
    private LocalDateTime updateDt;

    public void changeRole(GroupRole role) {
        this.role = role;
    }

    public void leave() {
        this.delAt = "Y";
    }
}
