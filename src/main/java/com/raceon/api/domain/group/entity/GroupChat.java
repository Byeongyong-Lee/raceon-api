package com.raceon.api.domain.group.entity;

import com.raceon.api.domain.group.enums.ChatMessageType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "group_chat")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class GroupChat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_idx")
    private Long chatIdx;

    @Column(name = "group_idx", nullable = false)
    private Long groupIdx;

    @Column(name = "sender_idx", nullable = false)
    private Long senderIdx;

    @Enumerated(EnumType.STRING)
    @Column(name = "message_type", nullable = false, length = 10)
    @Builder.Default
    private ChatMessageType messageType = ChatMessageType.TEXT;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @CreationTimestamp
    @Column(name = "create_dt", nullable = false, updatable = false)
    private LocalDateTime createDt;
}
