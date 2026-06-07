package com.raceon.api.domain.auth.repository;

import com.raceon.api.domain.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByKakaoId(String kakaoId);
    Optional<User> findByNaverId(String naverId);
    Optional<User> findByGoogleId(String googleId);
}
