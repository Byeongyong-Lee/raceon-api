package com.raceon.api.domain.group.controller;

import com.raceon.api.domain.group.dto.BoardCreateRequest;
import com.raceon.api.domain.group.dto.BoardResponse;
import com.raceon.api.domain.group.dto.CommentCreateRequest;
import com.raceon.api.domain.group.entity.GroupBoard;
import com.raceon.api.domain.group.entity.GroupBoardComment;
import com.raceon.api.domain.group.service.GroupBoardService;
import com.raceon.api.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/groups/{groupIdx}")
@RequiredArgsConstructor
public class GroupBoardController {

    private final GroupBoardService groupBoardService;

    @GetMapping("/boards")
    public ResponseEntity<ApiResponse<Page<BoardResponse>>> getPosts(Authentication auth,
                                                                      @PathVariable Long groupIdx,
                                                                      @RequestParam(defaultValue = "0") int page,
                                                                      @RequestParam(defaultValue = "20") int size) {
        Long userIdx = Long.parseLong(auth.getName());
        PageRequest pageable = PageRequest.of(page, size, Sort.by("createDt").descending());
        Page<BoardResponse> result = groupBoardService.getPosts(groupIdx, userIdx, pageable)
                .map(BoardResponse::new);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @GetMapping("/boards/notices")
    public ResponseEntity<ApiResponse<List<BoardResponse>>> getNotices(Authentication auth,
                                                                        @PathVariable Long groupIdx) {
        Long userIdx = Long.parseLong(auth.getName());
        List<BoardResponse> list = groupBoardService.getNotices(groupIdx, userIdx)
                .stream().map(BoardResponse::new).toList();
        return ResponseEntity.ok(ApiResponse.ok(list));
    }

    @PostMapping("/boards")
    public ResponseEntity<ApiResponse<BoardResponse>> createPost(Authentication auth,
                                                                  @PathVariable Long groupIdx,
                                                                  @RequestBody BoardCreateRequest request) {
        Long userIdx = Long.parseLong(auth.getName());
        GroupBoard board = groupBoardService.createPost(groupIdx, userIdx, request.getTitle(), request.getContent());
        return ResponseEntity.ok(ApiResponse.ok(new BoardResponse(board)));
    }

    @GetMapping("/boards/{boardIdx}")
    public ResponseEntity<ApiResponse<BoardResponse>> getPost(Authentication auth,
                                                               @PathVariable Long groupIdx,
                                                               @PathVariable Long boardIdx) {
        Long userIdx = Long.parseLong(auth.getName());
        GroupBoard board = groupBoardService.getPost(groupIdx, userIdx, boardIdx);
        return ResponseEntity.ok(ApiResponse.ok(new BoardResponse(board)));
    }

    @PatchMapping("/boards/{boardIdx}")
    public ResponseEntity<ApiResponse<Void>> updatePost(Authentication auth,
                                                         @PathVariable Long groupIdx,
                                                         @PathVariable Long boardIdx,
                                                         @RequestBody BoardCreateRequest request) {
        Long userIdx = Long.parseLong(auth.getName());
        groupBoardService.updatePost(groupIdx, userIdx, boardIdx, request.getTitle(), request.getContent());
        return ResponseEntity.ok(ApiResponse.ok());
    }

    @DeleteMapping("/boards/{boardIdx}")
    public ResponseEntity<ApiResponse<Void>> deletePost(Authentication auth,
                                                         @PathVariable Long groupIdx,
                                                         @PathVariable Long boardIdx) {
        Long userIdx = Long.parseLong(auth.getName());
        groupBoardService.deletePost(groupIdx, userIdx, boardIdx);
        return ResponseEntity.ok(ApiResponse.ok());
    }

    @PatchMapping("/boards/{boardIdx}/notice")
    public ResponseEntity<ApiResponse<Void>> toggleNotice(Authentication auth,
                                                           @PathVariable Long groupIdx,
                                                           @PathVariable Long boardIdx,
                                                           @RequestBody Map<String, String> body) {
        Long userIdx = Long.parseLong(auth.getName());
        groupBoardService.toggleNotice(groupIdx, userIdx, boardIdx, body.get("isNotice"));
        return ResponseEntity.ok(ApiResponse.ok());
    }

    @GetMapping("/boards/{boardIdx}/comments")
    public ResponseEntity<ApiResponse<List<GroupBoardComment>>> getComments(Authentication auth,
                                                                             @PathVariable Long groupIdx,
                                                                             @PathVariable Long boardIdx) {
        Long userIdx = Long.parseLong(auth.getName());
        List<GroupBoardComment> comments = groupBoardService.getComments(groupIdx, userIdx, boardIdx);
        return ResponseEntity.ok(ApiResponse.ok(comments));
    }

    @PostMapping("/boards/{boardIdx}/comments")
    public ResponseEntity<ApiResponse<GroupBoardComment>> createComment(Authentication auth,
                                                                         @PathVariable Long groupIdx,
                                                                         @PathVariable Long boardIdx,
                                                                         @RequestBody CommentCreateRequest request) {
        Long userIdx = Long.parseLong(auth.getName());
        GroupBoardComment comment = groupBoardService.createComment(groupIdx, userIdx, boardIdx, request.getContent());
        return ResponseEntity.ok(ApiResponse.ok(comment));
    }

    @DeleteMapping("/boards/{boardIdx}/comments/{commentIdx}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(Authentication auth,
                                                            @PathVariable Long groupIdx,
                                                            @PathVariable Long boardIdx,
                                                            @PathVariable Long commentIdx) {
        Long userIdx = Long.parseLong(auth.getName());
        groupBoardService.deleteComment(groupIdx, userIdx, commentIdx);
        return ResponseEntity.ok(ApiResponse.ok());
    }
}
