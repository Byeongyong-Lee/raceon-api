package com.raceon.api.domain.user.service;

import com.raceon.api.domain.auth.entity.User;
import com.raceon.api.domain.auth.repository.UserRepository;
import com.raceon.api.domain.user.dto.UserResponse;
import com.raceon.api.global.upload.FileUploadService;
import com.raceon.api.global.upload.Image;
import com.raceon.api.global.upload.ImageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final FileUploadService fileUploadService;

    public UserResponse getMe(Long userIdx) {
        User user = userRepository.findById(userIdx)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
        return new UserResponse(user);
    }

    @Transactional
    public ImageResponse uploadProfileImage(Long userIdx, MultipartFile file) {
        User user = userRepository.findById(userIdx)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
        Image image = fileUploadService.uploadProfileImage(file, userIdx);
        user.updateProfileImage(image.getFilePath());
        return new ImageResponse(image);
    }
}
