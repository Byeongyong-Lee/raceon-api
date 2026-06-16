package com.raceon.api.global.websocket;

import com.raceon.api.domain.group.entity.GroupChat;
import com.raceon.api.domain.group.service.GroupChatService;
import com.raceon.api.domain.group.service.GroupRaceService;
import com.raceon.api.domain.group.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class GroupWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final GroupChatService groupChatService;
    private final GroupRaceService groupRaceService;
    private final GroupService groupService;

    @MessageMapping("/groups/{groupIdx}/chat")
    public void handleChat(@DestinationVariable Long groupIdx,
                           ChatMessage message,
                           SimpMessageHeaderAccessor headerAccessor) {
        Long senderIdx = getUserIdx(headerAccessor);
        groupService.validateMember(groupIdx, senderIdx);

        GroupChat saved = groupChatService.save(groupIdx, senderIdx, message.getContent(), message.getMessageType());
        messagingTemplate.convertAndSend("/sub/groups/" + groupIdx + "/chat", new ChatResponse(saved));
    }

    @MessageMapping("/groups/{groupIdx}/location/{groupRaceIdx}")
    public void handleLocation(@DestinationVariable Long groupIdx,
                               @DestinationVariable Long groupRaceIdx,
                               LocationMessage message,
                               SimpMessageHeaderAccessor headerAccessor) {
        Long userIdx = getUserIdx(headerAccessor);
        groupService.validateMember(groupIdx, userIdx);

        if (!groupRaceService.isLocationShareActive(groupRaceIdx)) {
            messagingTemplate.convertAndSendToUser(
                    String.valueOf(userIdx), "/queue/errors",
                    "위치공유가 활성화된 시간이 아닙니다.");
            return;
        }

        LocationBroadcast broadcast = new LocationBroadcast(
                userIdx,
                message.getLatitude(),
                message.getLongitude(),
                message.getAccuracy(),
                message.getTimestamp());
        messagingTemplate.convertAndSend("/sub/groups/" + groupIdx + "/location/" + groupRaceIdx, broadcast);
    }

    private Long getUserIdx(SimpMessageHeaderAccessor headerAccessor) {
        String name = headerAccessor.getUser() != null ? headerAccessor.getUser().getName() : null;
        if (name == null) throw new IllegalArgumentException("인증이 필요합니다.");
        return Long.parseLong(name);
    }
}
