package com.raceon.api.domain.group.service;

import com.raceon.api.domain.group.entity.GroupBoard;
import com.raceon.api.domain.group.entity.GroupBoardComment;
import com.raceon.api.domain.group.entity.GroupManagerPermission;
import com.raceon.api.domain.group.entity.GroupMember;
import com.raceon.api.domain.group.enums.GroupRole;
import com.raceon.api.domain.group.repository.GroupBoardCommentRepository;
import com.raceon.api.domain.group.repository.GroupBoardRepository;
import com.raceon.api.domain.group.repository.GroupManagerPermissionRepository;
import com.raceon.api.domain.group.repository.GroupMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GroupBoardService {

    private final GroupBoardRepository boardRepository;
    private final GroupBoardCommentRepository commentRepository;
    private final GroupMemberRepository memberRepository;
    private final GroupManagerPermissionRepository permissionRepository;
    private final GroupService groupService;

    public Page<GroupBoard> getPosts(Long groupIdx, Long userIdx, Pageable pageable) {
        groupService.validateMember(groupIdx, userIdx);
        return boardRepository.findByGroupIdxAndDelAt(groupIdx, "N", pageable);
    }

    public List<GroupBoard> getNotices(Long groupIdx, Long userIdx) {
        groupService.validateMember(groupIdx, userIdx);
        return boardRepository.findByGroupIdxAndIsNoticeAndDelAt(groupIdx, "Y", "N");
    }

    public GroupBoard getPost(Long groupIdx, Long userIdx, Long boardIdx) {
        groupService.validateMember(groupIdx, userIdx);
        return boardRepository.findByBoardIdxAndDelAt(boardIdx, "N")
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));
    }

    @Transactional
    public GroupBoard createPost(Long groupIdx, Long userIdx, String title, String content) {
        groupService.validateMember(groupIdx, userIdx);
        GroupBoard board = GroupBoard.builder()
                .groupIdx(groupIdx)
                .authorIdx(userIdx)
                .title(title)
                .content(content)
                .build();
        return boardRepository.save(board);
    }

    @Transactional
    public void updatePost(Long groupIdx, Long userIdx, Long boardIdx, String title, String content) {
        GroupBoard board = boardRepository.findByBoardIdxAndDelAt(boardIdx, "N")
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));
        if (!board.getAuthorIdx().equals(userIdx) && !hasBoardPermission(groupIdx, userIdx)) {
            throw new IllegalArgumentException("수정 권한이 없습니다.");
        }
        board.update(title, content);
    }

    @Transactional
    public void deletePost(Long groupIdx, Long userIdx, Long boardIdx) {
        GroupBoard board = boardRepository.findByBoardIdxAndDelAt(boardIdx, "N")
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));
        if (!board.getAuthorIdx().equals(userIdx) && !hasBoardPermission(groupIdx, userIdx)) {
            throw new IllegalArgumentException("삭제 권한이 없습니다.");
        }
        board.delete();
    }

    @Transactional
    public void toggleNotice(Long groupIdx, Long userIdx, Long boardIdx, String isNotice) {
        if (!hasBoardPermission(groupIdx, userIdx)) {
            throw new IllegalArgumentException("공지 설정 권한이 없습니다.");
        }
        GroupBoard board = boardRepository.findByBoardIdxAndDelAt(boardIdx, "N")
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));
        board.toggleNotice(isNotice);
    }

    public List<GroupBoardComment> getComments(Long groupIdx, Long userIdx, Long boardIdx) {
        groupService.validateMember(groupIdx, userIdx);
        return commentRepository.findByBoardIdxAndDelAt(boardIdx, "N");
    }

    @Transactional
    public GroupBoardComment createComment(Long groupIdx, Long userIdx, Long boardIdx, String content) {
        groupService.validateMember(groupIdx, userIdx);
        boardRepository.findByBoardIdxAndDelAt(boardIdx, "N")
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));
        GroupBoardComment comment = GroupBoardComment.builder()
                .boardIdx(boardIdx)
                .authorIdx(userIdx)
                .content(content)
                .build();
        return commentRepository.save(comment);
    }

    @Transactional
    public void deleteComment(Long groupIdx, Long userIdx, Long commentIdx) {
        GroupBoardComment comment = commentRepository.findByCommentIdxAndDelAt(commentIdx, "N")
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다."));
        if (!comment.getAuthorIdx().equals(userIdx) && !hasBoardPermission(groupIdx, userIdx)) {
            throw new IllegalArgumentException("삭제 권한이 없습니다.");
        }
        comment.delete();
    }

    private boolean hasBoardPermission(Long groupIdx, Long userIdx) {
        GroupMember member = memberRepository.findByGroupIdxAndUserIdxAndDelAt(groupIdx, userIdx, "N")
                .orElse(null);
        if (member == null) return false;
        if (member.getRole() == GroupRole.OWNER) return true;
        if (member.getRole() == GroupRole.MANAGER) {
            return permissionRepository.findByGroupIdxAndUserIdx(groupIdx, userIdx)
                    .map(p -> "Y".equals(p.getCanManageBoard()))
                    .orElse(false);
        }
        return false;
    }
}
