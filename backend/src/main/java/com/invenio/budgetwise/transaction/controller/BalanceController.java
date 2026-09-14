package com.invenio.budgetwise.transaction.controller;

import com.invenio.budgetwise.transaction.dto.BalanceResponse;
import com.invenio.budgetwise.transaction.service.BalanceService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * GET /api/balance (issue #15). La issue dice /balance; va bajo /api igual que
 * el resto (/api/auth, /api/transactions) para no dejar una ruta suelta con
 * otra convencion. El controller solo delega: el calculo vive en BalanceService.
 */
@RestController
public class BalanceController {

    private final BalanceService balanceService;

    public BalanceController(BalanceService balanceService) {
        this.balanceService = balanceService;
    }

    @GetMapping("/api/balance")
    public BalanceResponse saldo(Authentication authentication) {
        return balanceService.saldoDe(authentication.getName());
    }
}
