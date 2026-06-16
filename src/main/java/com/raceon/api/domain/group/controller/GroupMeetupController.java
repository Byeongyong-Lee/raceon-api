package com.raceon.api.domain.group.controller;

import com.raceon.api.domain.group.dto.MeetupCreateRequest;
import com.raceon.api.domain.group.dto.MeetupRespondRequest;
import com.raceon.api.domain.group.dto.MeetupResponse;
import com.raceon.api.domain.group.entity.GroupMeetup;
import com.raceon.api.domain.group.entity.GroupMeetupParticipant;
import com.raceon.api.domain.group.service.GroupMeetupService;
import com.raceon.api.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups/{groupIdx}/meetups")
@RequiredArgsConstructor
public class GroupMeetupController {

    private final GroupMeetupService groupMeetupService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<MeetupResponse>>> getMeetups(Authentication auth,
                                                                         @PathVariable Long groupIdx) {
        Long userIdx = Long.parseLong(auth.getName());
        List<MeetupResponse> list = groupMeetupService.getMeetups(groupIdx, userIdx)
                .stream().map(MeetupResponse::new).toList();
        return ResponseEntity.ok(ApiResponse.ok(list));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<MeetupResponse>> create(Authentication auth,
                                                               @PathVariable Long groupIdx,
                                                               @RequestBody MeetupCreateRequest request) {
        Long userIdx = Long.parseLong(auth.getName());
        GroupMeetup meetup = groupMeetupService.create(
                groupIdx, userIdx, request.getTitle(), request.getDescription(),
                request.getMeetupDt(), request.getLocation());
        return ResponseEntity.ok(ApiResponse.ok(new MeetupResponse(meetup)));
    }

    @PatchMapping("/{meetupIdx}")
    public ResponseEntity<ApiResponse<Void>> update(Authentication auth,
                                                     @PathVariable Long groupIdx,
                                                     @PathVariable Long meetupIdx,
                                                     @RequestBody MeetupCreateRequest request) {
        Long userIdx = Long.parseLong(auth.getName());
        groupMeetupService.update(groupIdx, userIdx, meetupIdx,
                request.getTitle(), request.getDescription(),
                request.getMeetupDt(), request.getLocation());
        return ResponseEntity.ok(ApiResponse.ok());
    }

    @DeleteMapping("/{meetupIdx}")
    public ResponseEntity<ApiResponse<Void>> delete(Authentication auth,
                                                     @PathVariable Long groupIdx,
                                                     @PathVariable Long meetupIdx) {
        Long userIdx = Long.parseLong(auth.getName());
        groupMeetupService.delete(groupIdx, userIdx, meetupIdx);
        return ResponseEntity.ok(ApiResponse.ok());
    }

    @PostMapping("/{meetupIdx}/respond")
    public ResponseEntity<ApiResponse<GroupMeetupParticipant>> respond(Authentication auth,
                                                                        @PathVariable Long groupIdx,
                                                                        @PathVariable Long meetupIdx,
                                                                        @RequestBody MeetupRespondRequest request) {
        Long userIdx = Long.parseLong(auth.getName());
        GroupMeetupParticipant participant = groupMeetupService.respond(groupIdx, userIdx, meetupIdx, request.getStatus());
        return ResponseEntity.ok(ApiResponse.ok(participant));
    }

    @GetMapping("/{meetupIdx}/participants")
    public ResponseEntity<ApiResponse<List<GroupMeetupParticipant>>> getParticipants(Authentication auth,
                                                                                      @PathVariable Long groupIdx,
                                                                                      @PathVariable Long meetupIdx) {
        Long userIdx = Long.parseLong(auth.getName());
        List<GroupMeetupParticipant> participants = groupMeetupService.getParticipants(groupIdx, userIdx, meetupIdx);
        return ResponseEntity.ok(ApiResponse.ok(participants));
    }
}
