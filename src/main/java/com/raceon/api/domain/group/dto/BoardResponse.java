package com.raceon.api.domain.group.dto;

import com.raceon.api.domain.group.entity.GroupBoard;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class BoardResponse {
    private final Long boardIdx;
    private final Long groupIdx;
    private final Long authorIdx;
    private final String title;
    private final String content;
    private final String isNotice;
    private final LocalDateTime createDt;
    private final LocalDateTime updateDt;

    public BoardResponse(GroupBoard board) {
        this.boardIdx = board.getBoardIdx();
        this.groupIdx = board.getGroupIdx();
        this.authorIdx = board.getAuthorIdx();
        this.title = board.getTitle();
        this.content = board.getContent();
        this.isNotice = board.getIsNotice();
        this.createDt = board.getCreateDt();
        this.updateDt = board.getUpdateDt();
    }
}
