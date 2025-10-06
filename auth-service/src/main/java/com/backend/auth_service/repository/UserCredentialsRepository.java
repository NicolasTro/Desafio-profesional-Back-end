package com.backend.auth_service.repository;

import com.backend.auth_service.model.domain.UserCredentials;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserCredentialsRepository extends JpaRepository<UserCredentials, String> {


    /**
     * Busca un usuario por su email.
     *
     * @param email El email del usuario a buscar.
     * @return Un Optional que contiene el usuario si se encuentra, o vacío si no.
     */

    Optional<UserCredentials> findByEmail(String email);


    /**
     * Busca un usuario por su userId.
     */
    Optional<UserCredentials> findByUserId(String userId);
}
