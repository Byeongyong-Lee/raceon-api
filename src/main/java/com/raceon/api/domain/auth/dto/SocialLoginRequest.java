package com.raceon.api.domain.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SocialLoginRequest {
    private String socialId;
    private String nickname;
    private String profileImage;
    private String gender;
    private String age;
    private String birthday;
    private String phone;
}
