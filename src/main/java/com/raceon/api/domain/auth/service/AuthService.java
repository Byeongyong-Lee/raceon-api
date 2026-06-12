package com.raceon.api.domain.auth.service;

import com.raceon.api.domain.auth.dto.LoginResponse;
import com.raceon.api.domain.auth.dto.SocialLoginRequest;
import com.raceon.api.domain.auth.dto.TokenRefreshResponse;
import com.raceon.api.domain.auth.entity.User;
import com.raceon.api.domain.auth.repository.UserRepository;
import com.raceon.api.domain.auth.repository.UserSearchCondition;
import com.raceon.api.global.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService implements UserDetailsService {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    @Transactional
    public LoginResponse kakaoLogin(SocialLoginRequest request) {
        User user = userRepository.findOne(UserSearchCondition.builder()
                        .kakaoId(request.getSocialId()).build())
                .orElseGet(() -> userRepository.save(User.builder()
                        .kakaoId(request.getSocialId())
                        .nickname(request.getNickname())
                        .profileImage(request.getProfileImage())
                        .gender(request.getGender())
                        .birthday(request.getBirthday())
                        .phone(request.getPhone())
                        .build()));
        return issueTokens(user);
    }

    @Transactional
    public LoginResponse naverLogin(SocialLoginRequest request) {
        User user = userRepository.findOne(UserSearchCondition.builder()
                        .naverId(request.getSocialId()).build())
                .orElseGet(() -> userRepository.save(User.builder()
                        .naverId(request.getSocialId())
                        .nickname(request.getNickname())
                        .profileImage(request.getProfileImage())
                        .gender(request.getGender())
                        .age(request.getAge())
                        .birthday(parseNaverBirthday(request.getBirthday()))
                        .phone(request.getPhone())
                        .build()));
        return issueTokens(user);
    }

    @Transactional
    public LoginResponse googleLogin(SocialLoginRequest request) {
        User user = userRepository.findOne(UserSearchCondition.builder()
                        .googleId(request.getSocialId()).build())
                .orElseGet(() -> userRepository.save(User.builder()
                        .googleId(request.getSocialId())
                        .nickname(request.getNickname())
                        .profileImage(request.getProfileImage())
                        .build()));
        return issueTokens(user);
    }

    @Transactional
    public TokenRefreshResponse refresh(String refreshToken) {
        if (!jwtProvider.validateToken(refreshToken) || !jwtProvider.isRefreshToken(refreshToken)) {
            throw new BadCredentialsException("유효하지 않은 리프레시 토큰입니다.");
        }

        Long userIdx = jwtProvider.getUserId(refreshToken);
        User user = userRepository.findById(userIdx)
                .orElseThrow(() -> new BadCredentialsException("존재하지 않는 유저입니다."));

        if (!refreshToken.equals(user.getRefreshToken())) {
            throw new BadCredentialsException("리프레시 토큰이 일치하지 않습니다.");
        }

        return new TokenRefreshResponse(jwtProvider.generateAccessToken(userIdx));
    }

    private LoginResponse issueTokens(User user) {
        String accessToken  = jwtProvider.generateAccessToken(user.getUserIdx());
        String refreshToken = jwtProvider.generateRefreshToken(user.getUserIdx());
        user.updateRefreshToken(refreshToken);
        return new LoginResponse(accessToken, refreshToken, user);
    }

    private String parseNaverBirthday(String birthday) {
        if (birthday == null) return null;
        return birthday.replace("-", "");
    }

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        User user = userRepository.findById(Long.parseLong(userId))
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userId));
        return new org.springframework.security.core.userdetails.User(
                String.valueOf(user.getUserIdx()),
                "",
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
        );
    }
}
