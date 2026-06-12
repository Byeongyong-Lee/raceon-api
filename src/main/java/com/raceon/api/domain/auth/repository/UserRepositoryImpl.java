package com.raceon.api.domain.auth.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.raceon.api.domain.auth.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.raceon.api.domain.auth.entity.QUser.user;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<User> findOne(UserSearchCondition condition) {
        return Optional.ofNullable(
                queryFactory.selectFrom(user)
                        .where(
                                kakaoIdEq(condition.getKakaoId()),
                                naverIdEq(condition.getNaverId()),
                                googleIdEq(condition.getGoogleId())
                        )
                        .fetchOne()
        );
    }

    private BooleanExpression kakaoIdEq(String kakaoId) {
        return kakaoId != null ? user.kakaoId.eq(kakaoId) : null;
    }

    private BooleanExpression naverIdEq(String naverId) {
        return naverId != null ? user.naverId.eq(naverId) : null;
    }

    private BooleanExpression googleIdEq(String googleId) {
        return googleId != null ? user.googleId.eq(googleId) : null;
    }

}
