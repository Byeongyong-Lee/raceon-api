package com.raceon.api.domain.auth.service;

import com.raceon.api.domain.auth.dto.LoginResponse;
import com.raceon.api.domain.auth.dto.SocialLoginRequest;
import com.raceon.api.domain.auth.entity.User;
import com.raceon.api.domain.auth.repository.UserRepository;
import com.raceon.api.global.jwt.JwtProvider;
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

    @Transactional
    public LoginResponse kakaoLogin(SocialLoginRequest request) {
        User user = userRepository.findByKakaoId(request.getSocialId())
                .orElseGet(() -> userRepository.save(User.builder()
                        .kakaoId(request.getSocialId())
                        .nickname(request.getNickname())
                        .profileImage(request.getProfileImage())
                        .gender(request.getGender())
                        .birthday(request.getBirthday())
                        .phone(request.getPhone())
                        .build()));
        return new LoginResponse(jwtProvider.generateToken(user.getUserIdx()), user);
    }

    @Transactional
    public LoginResponse naverLogin(SocialLoginRequest request) {
        User user = userRepository.findByNaverId(request.getSocialId())
                .orElseGet(() -> userRepository.save(User.builder()
                        .naverId(request.getSocialId())
                        .nickname(request.getNickname())
                        .profileImage(request.getProfileImage())
                        .gender(request.getGender())
                        .age(request.getAge())
                        .birthday(parseNaverBirthday(request.getBirthday()))
                        .phone(request.getPhone())
                        .build()));
        return new LoginResponse(jwtProvider.generateToken(user.getUserIdx()), user);
    }

    // 네이버 SDK는 birthday를 "MM-DD" 형식으로 전달 → "MMDD"로 변환
    private String parseNaverBirthday(String birthday) {
        if (birthday == null) return null;
        return birthday.replace("-", "");
    }

    @Transactional
    public LoginResponse googleLogin(SocialLoginRequest request) {
        User user = userRepository.findByGoogleId(request.getSocialId())
                .orElseGet(() -> userRepository.save(User.builder()
                        .googleId(request.getSocialId())
                        .nickname(request.getNickname())
                        .profileImage(request.getProfileImage())
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
