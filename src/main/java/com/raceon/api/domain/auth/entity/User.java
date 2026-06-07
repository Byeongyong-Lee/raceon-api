package com.raceon.api.domain.auth.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_idx")
    private Long userIdx;

    @Column(length = 50)
    private String nickname;

    @Column(columnDefinition = "TEXT")
    private String profileImage;

    @Column(unique = true, length = 200)
    private String kakaoId;

    @Column(unique = true, length = 200)
    private String naverId;

    @Column(unique = true, length = 200)
    private String googleId;

    @Column(length = 1)
    private String gender;

    @Column(length = 10)
    private String age;

    @Column(length = 4)
    private String birthday;

    @Column(length = 20)
    private String phone;

    @Column(columnDefinition = "TEXT")
    private String fcmToken;

    @Column(nullable = false, length = 10)
    @Builder.Default
    private String role = "USER";

    @CreationTimestamp
    @Column(name = "create_dt", updatable = false, nullable = false)
    private LocalDateTime createDt;

    @UpdateTimestamp
    @Column(name = "update_dt", nullable = false)
    private LocalDateTime updateDt;

    public void updateFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public void updateProfile(String nickname, String profileImage) {
        this.nickname = nickname;
        this.profileImage = profileImage;
    }
}
