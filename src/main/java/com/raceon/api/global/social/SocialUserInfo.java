package com.raceon.api.global.social;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SocialUserInfo {
    private String socialId;
    private String nickname;
    private String profileImage;
    private String gender;
    private String age;
    private String birthday;
    private String phone;
}
