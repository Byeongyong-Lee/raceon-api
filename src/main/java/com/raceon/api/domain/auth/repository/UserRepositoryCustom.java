package com.raceon.api.domain.auth.repository;

import com.raceon.api.domain.auth.entity.User;

import java.util.Optional;

public interface UserRepositoryCustom {
    Optional<User> findOne(UserSearchCondition condition);
}
