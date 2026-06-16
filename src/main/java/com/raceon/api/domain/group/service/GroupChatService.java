package com.raceon.api.domain.group.service;

import com.raceon.api.domain.group.entity.GroupChat;
import com.raceon.api.domain.group.enums.ChatMessageType;
import com.raceon.api.domain.group.repository.GroupChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GroupChatService {

    private final GroupChatRepository chatRepository;
    private final GroupService groupService;

    public List<GroupChat> getHistory(Long groupIdx, Long userIdx, Long cursorIdx, int size) {
        groupService.validateMember(groupIdx, userIdx);
        PageRequest page = PageRequest.of(0, size);
        if (cursorIdx == null) {
            return chatRepository.findByGroupIdxOrderByCreateDtDesc(groupIdx, page);
        }
        return chatRepository.findByGroupIdxAndChatIdxLessThanOrderByCreateDtDesc(groupIdx, cursorIdx, page);
    }

    @Transactional
    public GroupChat save(Long groupIdx, Long senderIdx, String content, ChatMessageType type) {
        GroupChat chat = GroupChat.builder()
                .groupIdx(groupIdx)
                .senderIdx(senderIdx)
                .content(content)
                .messageType(type)
                .build();
        return chatRepository.save(chat);
    }
}
