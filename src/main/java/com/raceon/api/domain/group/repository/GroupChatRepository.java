package com.raceon.api.domain.group.repository;

import com.raceon.api.domain.group.entity.GroupChat;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupChatRepository extends JpaRepository<GroupChat, Long> {
    List<GroupChat> findByGroupIdxOrderByCreateDtDesc(Long groupIdx, Pageable pageable);
    List<GroupChat> findByGroupIdxAndChatIdxLessThanOrderByCreateDtDesc(Long groupIdx, Long cursorIdx, Pageable pageable);
}
