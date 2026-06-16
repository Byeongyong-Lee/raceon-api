package com.raceon.api.domain.group.service;

import com.raceon.api.domain.group.entity.GroupMeetup;
import com.raceon.api.domain.group.entity.GroupMeetupParticipant;
import com.raceon.api.domain.group.entity.GroupManagerPermission;
import com.raceon.api.domain.group.entity.GroupMember;
import com.raceon.api.domain.group.enums.GroupRole;
import com.raceon.api.domain.group.enums.MeetupStatus;
import com.raceon.api.domain.group.repository.GroupManagerPermissionRepository;
import com.raceon.api.domain.group.repository.GroupMeetupParticipantRepository;
import com.raceon.api.domain.group.repository.GroupMeetupRepository;
import com.raceon.api.domain.group.repository.GroupMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GroupMeetupService {

    private final GroupMeetupRepository meetupRepository;
    private final GroupMeetupParticipantRepository participantRepository;
    private final GroupMemberRepository memberRepository;
    private final GroupManagerPermissionRepository permissionRepository;
    private final GroupService groupService;

    public List<GroupMeetup> getMeetups(Long groupIdx, Long userIdx) {
        groupService.validateMember(groupIdx, userIdx);
        return meetupRepository.findUpcomingByGroupIdx(groupIdx, LocalDateTime.now());
    }

    @Transactional
    public GroupMeetup create(Long groupIdx, Long userIdx, String title, String description,
                               LocalDateTime meetupDt, String location) {
        validateMeetupPermission(groupIdx, userIdx);
        LocalDateTime now = LocalDateTime.now();
        if (meetupDt.isBefore(now)) {
            throw new IllegalArgumentException("과거 날짜로 약속을 만들 수 없습니다.");
        }
        if (meetupDt.isAfter(now.plusDays(10))) {
            throw new IllegalArgumentException("오늘로부터 10일 이내의 날짜만 설정 가능합니다.");
        }
        GroupMeetup meetup = GroupMeetup.builder()
                .groupIdx(groupIdx)
                .createdBy(userIdx)
                .title(title)
                .description(description)
                .meetupDt(meetupDt)
                .location(location)
                .build();
        return meetupRepository.save(meetup);
    }

    @Transactional
    public void update(Long groupIdx, Long userIdx, Long meetupIdx, String title,
                        String description, LocalDateTime meetupDt, String location) {
        validateMeetupPermission(groupIdx, userIdx);
        GroupMeetup meetup = meetupRepository.findByMeetupIdxAndDelAt(meetupIdx, "N")
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 약속입니다."));
        if (!meetup.getGroupIdx().equals(groupIdx)) {
            throw new IllegalArgumentException("해당 모임의 약속이 아닙니다.");
        }
        if (meetupDt != null) {
            LocalDateTime now = LocalDateTime.now();
            if (meetupDt.isBefore(now)) throw new IllegalArgumentException("과거 날짜로 변경할 수 없습니다.");
            if (meetupDt.isAfter(now.plusDays(10))) throw new IllegalArgumentException("10일 이내의 날짜만 설정 가능합니다.");
        }
        meetup.update(title, description, meetupDt, location);
    }

    @Transactional
    public void delete(Long groupIdx, Long userIdx, Long meetupIdx) {
        validateMeetupPermission(groupIdx, userIdx);
        GroupMeetup meetup = meetupRepository.findByMeetupIdxAndDelAt(meetupIdx, "N")
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 약속입니다."));
        if (!meetup.getGroupIdx().equals(groupIdx)) {
            throw new IllegalArgumentException("해당 모임의 약속이 아닙니다.");
        }
        meetup.delete();
    }

    @Transactional
    public GroupMeetupParticipant respond(Long groupIdx, Long userIdx, Long meetupIdx, MeetupStatus status) {
        groupService.validateMember(groupIdx, userIdx);
        meetupRepository.findByMeetupIdxAndDelAt(meetupIdx, "N")
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 약속입니다."));

        GroupMeetupParticipant participant = participantRepository
                .findByMeetupIdxAndUserIdx(meetupIdx, userIdx)
                .orElseGet(() -> GroupMeetupParticipant.builder()
                        .meetupIdx(meetupIdx)
                        .userIdx(userIdx)
                        .build());
        participant.updateStatus(status);
        return participantRepository.save(participant);
    }

    public List<GroupMeetupParticipant> getParticipants(Long groupIdx, Long userIdx, Long meetupIdx) {
        groupService.validateMember(groupIdx, userIdx);
        return participantRepository.findByMeetupIdx(meetupIdx);
    }

    private void validateMeetupPermission(Long groupIdx, Long userIdx) {
        GroupMember member = memberRepository.findByGroupIdxAndUserIdxAndDelAt(groupIdx, userIdx, "N")
                .orElseThrow(() -> new IllegalArgumentException("모임 멤버가 아닙니다."));
        if (member.getRole() == GroupRole.OWNER) return;
        if (member.getRole() == GroupRole.MANAGER) {
            GroupManagerPermission perm = permissionRepository.findByGroupIdxAndUserIdx(groupIdx, userIdx)
                    .orElseThrow(() -> new IllegalArgumentException("권한 정보가 없습니다."));
            if ("Y".equals(perm.getCanManageMeetup())) return;
        }
        throw new IllegalArgumentException("약속 관리 권한이 없습니다.");
    }
}
