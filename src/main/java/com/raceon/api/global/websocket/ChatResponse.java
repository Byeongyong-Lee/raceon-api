package com.raceon.api.global.websocket;

import com.raceon.api.domain.group.entity.GroupChat;
import com.raceon.api.domain.group.enums.ChatMessageType;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ChatResponse {
    private final Long chatIdx;
    private final Long senderIdx;
    private final String content;
    private final ChatMessageType messageType;
    private final LocalDateTime createDt;

    public ChatResponse(GroupChat chat) {
        this.chatIdx = chat.getChatIdx();
        this.senderIdx = chat.getSenderIdx();
        this.content = chat.getContent();
        this.messageType = chat.getMessageType();
        this.createDt = chat.getCreateDt();
    }
}
