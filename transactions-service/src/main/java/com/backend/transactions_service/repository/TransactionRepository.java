package com.backend.transactions_service.repository;

import com.backend.transactions_service.model.domain.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, String> {
    List<Transaction> findTop5ByAccountIdOrderByDatedDesc(String accountId);
}
