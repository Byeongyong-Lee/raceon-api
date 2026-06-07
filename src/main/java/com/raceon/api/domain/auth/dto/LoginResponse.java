package com.raceon.api.domain.auth.dto;

import com.raceon.api.domain.auth.entity.User;
import lombok.Getter;

@Getter
public class LoginResponse {
    private final String token;
    private final Long userId;
    private final String nickname;
    private final String profileImage;
    private final String role;

    public LoginResponse(String token, User user) {
        this.token = token;
        this.userId = user.getUserIdx();
        this.nickname = user.getNickname();
        this.profileImage = user.getProfileImage();
        this.role = user.getRole();
    }
}
