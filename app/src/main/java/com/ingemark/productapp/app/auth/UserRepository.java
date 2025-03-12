package com.ingemark.productapp.app.auth;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.ingemark.productapp.app.entity.identifiable.IdentifiableEntityRepository;

@Repository
public interface UserRepository extends IdentifiableEntityRepository<User, Integer>
{
    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);
}
