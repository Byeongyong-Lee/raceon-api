package com.raceon.api.domain.auth.dto;

import com.raceon.api.domain.auth.entity.User;
import lombok.Getter;

@Getter
public class LoginResponse {
    private final String accessToken;
    private final String refreshToken;
    private final Long userId;
    private final String nickname;
    private final String profileImage;
    private final String role;

    public LoginResponse(String accessToken, String refreshToken, User user) {
        this.accessToken  = accessToken;
        this.refreshToken = refreshToken;
        this.userId       = user.getUserIdx();
        this.nickname     = user.getNickname();
        this.profileImage = user.getProfileImage();
        this.role         = user.getRole();
    }
}
