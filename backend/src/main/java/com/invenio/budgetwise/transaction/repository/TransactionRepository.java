package com.invenio.budgetwise.transaction.repository;

import com.invenio.budgetwise.transaction.domain.Transaction;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Toda consulta filtra por usuario: nadie ve movimientos ajenos.
 * No agregues aca un metodo que devuelva transacciones sin filtrar.
 */
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByUserIdOrderByDateDesc(Long userId);

    Optional<Transaction> findByIdAndUserId(Long id, Long userId);

    List<Transaction> findByUserIdAndDateBetween(Long userId, LocalDate desde, LocalDate hasta);
}
