package com.raceon.api.domain.userrace.service;

import com.raceon.api.domain.auth.entity.User;
import com.raceon.api.domain.auth.repository.UserRepository;
import com.raceon.api.domain.race.entity.Race;
import com.raceon.api.domain.race.repository.RaceRepository;
import com.raceon.api.domain.userrace.dto.UserRaceRecordUpdateRequest;
import com.raceon.api.domain.userrace.dto.UserRaceRegisterRequest;
import com.raceon.api.domain.userrace.dto.UserRaceResponse;
import com.raceon.api.domain.userrace.entity.UserRace;
import com.raceon.api.domain.userrace.repository.UserRaceRepository;
import com.raceon.api.domain.userrace.repository.UserRaceSearchCondition;
import com.raceon.api.global.upload.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserRaceService {

    private final UserRaceRepository userRaceRepository;
    private final UserRepository userRepository;
    private final RaceRepository raceRepository;
    private final FileUploadService fileUploadService;

    @Transactional
    public UserRaceResponse register(Long userIdx, UserRaceRegisterRequest request) {
        if (userRaceRepository.existsActive(UserRaceSearchCondition.builder()
                .userIdx(userIdx)
                .raceIdx(request.getRaceIdx())
                .delAt("N")
                .build())) {
            throw new IllegalArgumentException("이미 등록된 대회입니다.");
        }
        User user = userRepository.findById(userIdx)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
        Race race = raceRepository.findById(request.getRaceIdx())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 대회입니다."));
        UserRace userRace = UserRace.builder()
                .user(user)
                .race(race)
                .course(request.getCourse())
                .build();
        return new UserRaceResponse(userRaceRepository.save(userRace));
    }

    @Transactional
    public void cancel(Long userIdx, Long userRaceIdx) {
        UserRace userRace = userRaceRepository.findById(userRaceIdx)
                .orElseThrow(() -> new IllegalArgumentException("등록 내역이 없습니다."));
        if (!userRace.getUser().getUserIdx().equals(userIdx)) {
            throw new IllegalArgumentException("본인의 대회만 취소할 수 있습니다.");
        }
        if ("Y".equals(userRace.getDelAt())) {
            throw new IllegalArgumentException("이미 취소된 대회입니다.");
        }
        userRace.cancel();
    }

    public List<UserRaceResponse> getMyRaces(Long userIdx) {
        return userRaceRepository.search(UserRaceSearchCondition.builder()
                        .userIdx(userIdx)
                        .delAt("N")
                        .build())
                .stream().map(UserRaceResponse::new).toList();
    }

    @Transactional
    public UserRaceResponse uploadRecordImage(Long userIdx, Long userRaceIdx, MultipartFile file) {
        UserRace userRace = userRaceRepository.findById(userRaceIdx)
                .orElseThrow(() -> new IllegalArgumentException("등록 내역이 없습니다."));
        if (!userRace.getUser().getUserIdx().equals(userIdx)) {
            throw new IllegalArgumentException("본인의 대회만 수정할 수 있습니다.");
        }
        if ("Y".equals(userRace.getDelAt())) {
            throw new IllegalArgumentException("취소된 대회는 수정할 수 없습니다.");
        }
        String imagePath = fileUploadService.uploadRecordImage(file, userIdx);
        userRace.updateRecordImagePath(imagePath);
        return new UserRaceResponse(userRace);
    }

    @Transactional
    public UserRaceResponse updateRecord(Long userIdx, Long userRaceIdx, UserRaceRecordUpdateRequest request) {
        UserRace userRace = userRaceRepository.findById(userRaceIdx)
                .orElseThrow(() -> new IllegalArgumentException("등록 내역이 없습니다."));
        if (!userRace.getUser().getUserIdx().equals(userIdx)) {
            throw new IllegalArgumentException("본인의 대회만 수정할 수 있습니다.");
        }
        if ("Y".equals(userRace.getDelAt())) {
            throw new IllegalArgumentException("취소된 대회는 수정할 수 없습니다.");
        }
        userRace.updateRecord(request);
        return new UserRaceResponse(userRace);
    }
}
