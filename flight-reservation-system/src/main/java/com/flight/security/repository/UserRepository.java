package com.flight.security.repository;

import com.flight.security.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> getUserByUsername(String username);
    boolean existsByUsername(String username);
}
