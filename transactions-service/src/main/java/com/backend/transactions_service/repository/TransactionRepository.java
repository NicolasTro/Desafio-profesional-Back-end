package com.backend.transactions_service.repository;

import com.backend.transactions_service.model.domain.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, String> {

    /**
     * 🔹 Últimas 5 transacciones por CVU (ordenadas por fecha descendente)
     */
    List<Transaction> findTop5ByAccountIdOrderByDatedDesc(String accountId);

    /**
     * 🔹 Todas las transacciones por CVU
     */
    List<Transaction> findByAccountId(String accountId);

    /**
     * 🔹 Todas las transacciones por CVU ordenadas por fecha descendente
     */
    List<Transaction> findByAccountIdOrderByDatedDesc(String accountId);

    /**
     * 🔹 Buscar transacción específica por ID y CVU
     */
    Optional<Transaction> findByIdAndAccountId(String id, String accountId);

    /**
     * Compatibility alias used by tests / older code: delegate to findTop5ByAccountIdOrderByDatedDesc
     */
    default List<Transaction> findLast5ByAccountId(String accountId) {
        return findTop5ByAccountIdOrderByDatedDesc(accountId);
    }
}
