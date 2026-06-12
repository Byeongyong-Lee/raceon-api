package com.raceon.api.domain.auth.repository;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserSearchCondition {
    private String kakaoId;
    private String naverId;
    private String googleId;
    private String jwtToken;
}
