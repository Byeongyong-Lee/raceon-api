package com.raceon.api.global.websocket;

import com.raceon.api.domain.group.enums.ChatMessageType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessage {
    private String content;
    private ChatMessageType messageType = ChatMessageType.TEXT;
}
