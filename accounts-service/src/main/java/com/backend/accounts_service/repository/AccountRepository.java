package com.backend.accounts_service.repository;

import com.backend.accounts_service.model.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, String> {


    /**
     * Find an account by its user ID.
     *
     * @param userId the user ID to search for
     * @return an Optional containing the found Account, or empty if not found
     */
    Optional<Account> findByUserId(String userId);

    /**
     * Find an account by its CVU (Clave Virtual Uniforme).
     *
     * @param cvu the CVU to search for
     * @return an Optional containing the found Account, or empty if not found
     */
    Optional<Account> findByCvu(String cvu);



}
