package com.raceon.api.domain.user.dto;

import com.raceon.api.domain.auth.entity.User;
import lombok.Getter;

@Getter
public class UserResponse {
    private final Long userIdx;
    private final String nickname;
    private final String profileImage;
    private final String gender;
    private final String age;
    private final String birthday;
    private final String phone;
    private final String role;

    public UserResponse(User user) {
        this.userIdx = user.getUserIdx();
        this.nickname = user.getNickname();
        this.profileImage = user.getProfileImage();
        this.gender = user.getGender();
        this.age = user.getAge();
        this.birthday = user.getBirthday();
        this.phone = user.getPhone();
        this.role = user.getRole();
    }
}
