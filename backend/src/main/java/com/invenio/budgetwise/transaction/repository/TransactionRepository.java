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

    /**
     * OrderByDateDescIdDesc (issue #15): con solo la fecha, dos movimientos
     * del mismo dia podian salir en cualquier orden entre una llamada y
     * otra, y la lista "saltaba" al agregar algo. Lo senalo Pablo revisando
     * el PR #53; se corrige aca porque el calculo del saldo reusa este mismo
     * metodo para sumar ingresos y gastos.
     */
    List<Transaction> findByUserIdOrderByDateDescIdDesc(Long userId);

    Optional<Transaction> findByIdAndUserId(Long id, Long userId);

    List<Transaction> findByUserIdAndDateBetween(Long userId, LocalDate desde, LocalDate hasta);
}
