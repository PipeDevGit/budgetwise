package com.invenio.budgetwise.transaction.controller;

import com.invenio.budgetwise.transaction.dto.BalanceResponse;
import com.invenio.budgetwise.transaction.service.TransactionService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Saldo automatico (issue #15): ingresos totales, gastos totales y el saldo,
 * calculados sobre las transacciones del usuario autenticado.
 *
 * Va en su propio controller, no colgado de TransactionController, porque la
 * issue pide un endpoint propio. La issue #15 dice literalmente "GET /balance",
 * pero se publica bajo /api, igual que el resto de la API (/api/auth,
 * /api/transactions): es el mismo desajuste que ya paso con la issue #4
 * (/auth vs /api/auth), documentado en la review del PR #49. SecurityConfig no
 * necesita cambios: no hay una ruta publica que agregar, "/api/**" ya exige
 * autenticacion por default (anyRequest().authenticated()).
 */
@RestController
@RequestMapping("/api/balance")
public class BalanceController {

    private final TransactionService transactionService;

    public BalanceController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    public BalanceResponse obtener(Authentication authentication) {
        return transactionService.calcularSaldo(authentication.getName());
    }
}
