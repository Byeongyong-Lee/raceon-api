package com.raceon.api.global.social;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class NaverSocialClient {

    private final RestClient restClient;

    public NaverSocialClient() {
        this.restClient = RestClient.builder()
                .baseUrl("https://openapi.naver.com")
                .build();
    }

    public SocialUserInfo getUserInfo(String accessToken) {
        NaverUserInfo info = restClient.get()
                .uri("/v1/nid/me")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .body(NaverUserInfo.class);

        if (info == null || info.response() == null) throw new IllegalStateException("Naver user info is null");

        NaverUserInfo.NaverResponse resp = info.response();
        String gender = resp.gender() != null
                ? (resp.gender().equals("M") ? "M" : "F") : null;

        return SocialUserInfo.builder()
                .socialId(resp.id())
                .nickname(resp.nickname())
                .profileImage(resp.profileImage())
                .gender(gender)
                .age(resp.age())
                .birthday(resp.birthday() != null ? resp.birthday().replace("-", "") : null)
                .phone(resp.mobile())
                .build();
    }

    private record NaverUserInfo(
            String resultcode,
            NaverResponse response
    ) {
        private record NaverResponse(
                String id,
                String nickname,
                @JsonProperty("profile_image") String profileImage,
                String gender,
                String age,
                String birthday,
                String mobile
        ) {}
    }
}
