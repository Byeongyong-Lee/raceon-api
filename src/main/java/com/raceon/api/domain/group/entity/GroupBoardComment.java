package com.raceon.api.domain.group.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "group_board_comment")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class GroupBoardComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_idx")
    private Long commentIdx;

    @Column(name = "board_idx", nullable = false)
    private Long boardIdx;

    @Column(name = "author_idx", nullable = false)
    private Long authorIdx;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "del_at", nullable = false, length = 1)
    @Builder.Default
    private String delAt = "N";

    @CreationTimestamp
    @Column(name = "create_dt", nullable = false, updatable = false)
    private LocalDateTime createDt;

    @UpdateTimestamp
    @Column(name = "update_dt", nullable = false)
    private LocalDateTime updateDt;

    public void delete() {
        this.delAt = "Y";
    }
}
