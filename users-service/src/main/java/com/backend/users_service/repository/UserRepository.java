package com.backend.users_service.repository;

import com.backend.users_service.model.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
        Optional<User> findByEmail(String email);
        Boolean existsByDni(String dni);

}
