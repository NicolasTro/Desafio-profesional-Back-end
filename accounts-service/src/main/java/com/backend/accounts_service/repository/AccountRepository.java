package com.backend.accounts_service.repository;

import com.backend.accounts_service.model.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, String> {
    Optional<Account> findByUserId(String userId);
    Optional<Account> findByCvu(String cvu);   // ✅ este es el que usamos en los servicios





}
