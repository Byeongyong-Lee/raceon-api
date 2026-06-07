package com.raceon.api.global.social;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class GoogleSocialClient {

    private final RestClient restClient;

    public GoogleSocialClient() {
        this.restClient = RestClient.builder()
                .baseUrl("https://www.googleapis.com")
                .build();
    }

    public SocialUserInfo getUserInfo(String accessToken) {
        GoogleUserInfo info = restClient.get()
                .uri("/oauth2/v3/userinfo")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .body(GoogleUserInfo.class);

        if (info == null) throw new IllegalStateException("Google user info is null");

        return SocialUserInfo.builder()
                .socialId(info.sub())
                .nickname(info.name())
                .profileImage(info.picture())
                .build();
    }

    private record GoogleUserInfo(
            String sub,
            String name,
            String picture
    ) {}
}
