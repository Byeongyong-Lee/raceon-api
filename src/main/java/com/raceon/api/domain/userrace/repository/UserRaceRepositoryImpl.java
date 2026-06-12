package com.raceon.api.domain.userrace.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.raceon.api.domain.userrace.entity.UserRace;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.raceon.api.domain.userrace.entity.QUserRace.userRace;

@Repository
@RequiredArgsConstructor
public class UserRaceRepositoryImpl implements UserRaceRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<UserRace> search(UserRaceSearchCondition condition) {
        return queryFactory
                .selectFrom(userRace)
                .where(
                        userIdxEq(condition.getUserIdx()),
                        raceIdxEq(condition.getRaceIdx()),
                        delAtEq(condition.getDelAt()),
                        courseEq(condition.getCourse()),
                        finishYnEq(condition.getFinishYn())
                )
                .orderBy(userRace.createDt.desc())
                .fetch();
    }

    @Override
    public boolean existsActive(UserRaceSearchCondition condition) {
        return queryFactory
                .selectOne()
                .from(userRace)
                .where(
                        userIdxEq(condition.getUserIdx()),
                        raceIdxEq(condition.getRaceIdx()),
                        delAtEq(condition.getDelAt())
                )
                .fetchFirst() != null;
    }

    private BooleanExpression userIdxEq(Long userIdx) {
        return userIdx != null ? userRace.user.userIdx.eq(userIdx) : null;
    }

    private BooleanExpression raceIdxEq(Long raceIdx) {
        return raceIdx != null ? userRace.race.raceIdx.eq(raceIdx) : null;
    }

    private BooleanExpression delAtEq(String delAt) {
        return delAt != null ? userRace.delAt.eq(delAt) : null;
    }

    private BooleanExpression courseEq(String course) {
        return course != null ? userRace.course.eq(course) : null;
    }

    private BooleanExpression finishYnEq(String finishYn) {
        return finishYn != null ? userRace.finishYn.eq(finishYn) : null;
    }
}
