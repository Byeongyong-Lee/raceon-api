package com.raceon.api.domain.race.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.raceon.api.domain.race.entity.Race;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.raceon.api.domain.race.entity.QRace.race;

@Repository
@RequiredArgsConstructor
public class RaceRepositoryImpl implements RaceRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Race> findOne(RaceSearchCondition condition) {
        return Optional.ofNullable(
                queryFactory.selectFrom(race)
                        .where(sourceIdEq(condition.getSourceId()))
                        .fetchOne()
        );
    }

    @Override
    public List<Race> search(RaceSearchCondition condition) {
        return queryFactory.selectFrom(race)
                .where(
                        raceDateGoe(condition.getRaceDateFrom()),
                        raceDateLoe(condition.getRaceDateTo())
                )
                .orderBy(race.raceDate.asc())
                .fetch();
    }

    private BooleanExpression sourceIdEq(String sourceId) {
        return sourceId != null ? race.sourceId.eq(sourceId) : null;
    }

    private BooleanExpression raceDateGoe(LocalDate from) {
        return from != null ? race.raceDate.goe(from) : null;
    }

    private BooleanExpression raceDateLoe(LocalDate to) {
        return to != null ? race.raceDate.loe(to) : null;
    }
}
