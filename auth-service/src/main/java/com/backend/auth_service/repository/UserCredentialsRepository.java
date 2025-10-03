package com.backend.auth_service.repository;

import com.backend.auth_service.model.domain.UserCredentials;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserCredentialsRepository extends JpaRepository<UserCredentials, String> {
    Optional<UserCredentials> findByEmail(String email);
    Optional<UserCredentials> findByUserId(String userId);
}
