package com.raceon.api.domain.group.controller;

import com.raceon.api.domain.group.entity.GroupChat;
import com.raceon.api.domain.group.service.GroupChatService;
import com.raceon.api.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups/{groupIdx}/chat")
@RequiredArgsConstructor
public class GroupChatController {

    private final GroupChatService groupChatService;

    @GetMapping("/messages")
    public ResponseEntity<ApiResponse<List<GroupChat>>> getHistory(Authentication auth,
                                                                    @PathVariable Long groupIdx,
                                                                    @RequestParam(required = false) Long cursor,
                                                                    @RequestParam(defaultValue = "50") int size) {
        Long userIdx = Long.parseLong(auth.getName());
        List<GroupChat> messages = groupChatService.getHistory(groupIdx, userIdx, cursor, size);
        return ResponseEntity.ok(ApiResponse.ok(messages));
    }
}
