package com.raceon.api.domain.auth.service;

import com.raceon.api.domain.auth.dto.LoginResponse;
import com.raceon.api.domain.auth.dto.SocialLoginRequest;
import com.raceon.api.domain.auth.entity.User;
import com.raceon.api.domain.auth.repository.UserRepository;
import com.raceon.api.global.jwt.JwtProvider;
import com.raceon.api.global.social.GoogleSocialClient;
import com.raceon.api.global.social.KakaoSocialClient;
import com.raceon.api.global.social.NaverSocialClient;
import com.raceon.api.global.social.SocialUserInfo;
import lombok.RequiredArgsConstructor;
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
    private final KakaoSocialClient kakaoSocialClient;
    private final NaverSocialClient naverSocialClient;
    private final GoogleSocialClient googleSocialClient;

    @Transactional
    public LoginResponse kakaoLogin(SocialLoginRequest request) {
        SocialUserInfo info = kakaoSocialClient.getUserInfo(request.getAccessToken());
        User user = userRepository.findByKakaoId(info.getSocialId())
                .orElseGet(() -> userRepository.save(User.builder()
                        .kakaoId(info.getSocialId())
                        .nickname(info.getNickname())
                        .profileImage(info.getProfileImage())
                        .gender(info.getGender())
                        .birthday(info.getBirthday())
                        .phone(info.getPhone())
                        .build()));
        return new LoginResponse(jwtProvider.generateToken(user.getUserIdx()), user);
    }

    @Transactional
    public LoginResponse naverLogin(SocialLoginRequest request) {
        SocialUserInfo info = naverSocialClient.getUserInfo(request.getAccessToken());
        User user = userRepository.findByNaverId(info.getSocialId())
                .orElseGet(() -> userRepository.save(User.builder()
                        .naverId(info.getSocialId())
                        .nickname(info.getNickname())
                        .profileImage(info.getProfileImage())
                        .gender(info.getGender())
                        .age(info.getAge())
                        .birthday(info.getBirthday())
                        .phone(info.getPhone())
                        .build()));
        return new LoginResponse(jwtProvider.generateToken(user.getUserIdx()), user);
    }

    @Transactional
    public LoginResponse googleLogin(SocialLoginRequest request) {
        SocialUserInfo info = googleSocialClient.getUserInfo(request.getAccessToken());
        User user = userRepository.findByGoogleId(info.getSocialId())
                .orElseGet(() -> userRepository.save(User.builder()
                        .googleId(info.getSocialId())
                        .nickname(info.getNickname())
                        .profileImage(info.getProfileImage())
                        .build()));
        return new LoginResponse(jwtProvider.generateToken(user.getUserIdx()), user);
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
