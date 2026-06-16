package com.raceon.api.domain.group.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "group_board")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class GroupBoard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_idx")
    private Long boardIdx;

    @Column(name = "group_idx", nullable = false)
    private Long groupIdx;

    @Column(name = "author_idx", nullable = false)
    private Long authorIdx;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "is_notice", nullable = false, length = 1)
    @Builder.Default
    private String isNotice = "N";

    @Column(name = "del_at", nullable = false, length = 1)
    @Builder.Default
    private String delAt = "N";

    @CreationTimestamp
    @Column(name = "create_dt", nullable = false, updatable = false)
    private LocalDateTime createDt;

    @UpdateTimestamp
    @Column(name = "update_dt", nullable = false)
    private LocalDateTime updateDt;

    public void update(String title, String content) {
        if (title != null) this.title = title;
        if (content != null) this.content = content;
    }

    public void toggleNotice(String isNotice) {
        this.isNotice = isNotice;
    }

    public void delete() {
        this.delAt = "Y";
    }
}
