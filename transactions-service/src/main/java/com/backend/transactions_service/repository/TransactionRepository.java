package com.backend.transactions_service.repository;

import com.backend.transactions_service.model.domain.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, String> {

    /**
     * Últimas 5 transacciones por CVU
     */
    List<Transaction> findLast5ByAccountId(String accountId);

    /**
     * Todas las transacciones por CVU
     */
    List<Transaction> findByAccountId(String accountId);


    /** * Todas las transacciones por CVU ordenadas por fecha descendente
     */
    List<Transaction> findByAccountIdOrderByDatedDesc(String accountId);
}
