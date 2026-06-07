package com.raceon.api.global.social;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class KakaoSocialClient {

    private final RestClient restClient;

    public KakaoSocialClient() {
        this.restClient = RestClient.builder()
                .baseUrl("https://kapi.kakao.com")
                .build();
    }

    public SocialUserInfo getUserInfo(String accessToken) {
        KakaoUserInfo info = restClient.get()
                .uri("/v2/user/me")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .body(KakaoUserInfo.class);

        if (info == null) throw new IllegalStateException("Kakao user info is null");

        KakaoUserInfo.KakaoAccount account = info.kakaoAccount();
        String nickname = account != null && account.profile() != null ? account.profile().nickname() : null;
        String profileImage = account != null && account.profile() != null ? account.profile().profileImageUrl() : null;
        String gender = account != null && account.gender() != null
                ? (account.gender().equals("male") ? "M" : "F") : null;
        String birthday = account != null ? account.birthday() : null;
        String phone = account != null ? account.phoneNumber() : null;

        return SocialUserInfo.builder()
                .socialId(String.valueOf(info.id()))
                .nickname(nickname)
                .profileImage(profileImage)
                .gender(gender)
                .birthday(birthday)
                .phone(phone)
                .build();
    }

    private record KakaoUserInfo(
            Long id,
            @JsonProperty("kakao_account") KakaoAccount kakaoAccount
    ) {
        private record KakaoAccount(
                KakaoProfile profile,
                String gender,
                String birthday,
                @JsonProperty("phone_number") String phoneNumber
        ) {
            private record KakaoProfile(
                    String nickname,
                    @JsonProperty("profile_image_url") String profileImageUrl
            ) {}
        }
    }
}
