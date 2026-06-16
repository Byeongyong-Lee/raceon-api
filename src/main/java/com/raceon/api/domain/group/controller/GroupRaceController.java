package com.raceon.api.domain.group.controller;

import com.raceon.api.domain.group.dto.GroupRaceRequest;
import com.raceon.api.domain.group.dto.LocationShareTimeRequest;
import com.raceon.api.domain.group.entity.GroupRace;
import com.raceon.api.domain.group.service.GroupRaceService;
import com.raceon.api.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups/{groupIdx}/races")
@RequiredArgsConstructor
public class GroupRaceController {

    private final GroupRaceService groupRaceService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<GroupRace>>> getRaces(Authentication auth,
                                                                  @PathVariable Long groupIdx) {
        Long userIdx = Long.parseLong(auth.getName());
        List<GroupRace> races = groupRaceService.getRaces(groupIdx, userIdx);
        return ResponseEntity.ok(ApiResponse.ok(races));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<GroupRace>> linkRace(Authentication auth,
                                                            @PathVariable Long groupIdx,
                                                            @RequestBody GroupRaceRequest request) {
        Long userIdx = Long.parseLong(auth.getName());
        GroupRace groupRace = groupRaceService.linkRace(groupIdx, userIdx, request.getRaceIdx());
        return ResponseEntity.ok(ApiResponse.ok(groupRace));
    }

    @DeleteMapping("/{groupRaceIdx}")
    public ResponseEntity<ApiResponse<Void>> unlinkRace(Authentication auth,
                                                         @PathVariable Long groupIdx,
                                                         @PathVariable Long groupRaceIdx) {
        Long userIdx = Long.parseLong(auth.getName());
        groupRaceService.unlinkRace(groupIdx, userIdx, groupRaceIdx);
        return ResponseEntity.ok(ApiResponse.ok());
    }

    @PatchMapping("/{groupRaceIdx}/location-share")
    public ResponseEntity<ApiResponse<GroupRace>> updateLocationShareTime(Authentication auth,
                                                                           @PathVariable Long groupIdx,
                                                                           @PathVariable Long groupRaceIdx,
                                                                           @RequestBody LocationShareTimeRequest request) {
        Long userIdx = Long.parseLong(auth.getName());
        GroupRace groupRace = groupRaceService.updateLocationShareTime(
                groupIdx, userIdx, groupRaceIdx,
                request.getLocationShareStart(), request.getLocationShareEnd());
        return ResponseEntity.ok(ApiResponse.ok(groupRace));
    }
}
