package com.invenio.budgetwise.transaction.controller;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BalanceController {

    private final TransactionRepository repository;

    public BalanceController(TransactionRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/balance")
    public BalanceResponse getBalance(@RequestParam Long userId) {
        List<Transaction> all = repository.findAll();

        BigDecimal ingresos = BigDecimal.ZERO;
        BigDecimal gastos = BigDecimal.ZERO;

        for (Transaction t : all) {
            if (t.getType().equals("INGRESO")) {
                ingresos = ingresos.add(t.getAmount());
            } else {
                gastos = gastos.add(t.getAmount());
            }
        }

        BigDecimal saldo = ingresos.subtract(gastos);
        return new BalanceResponse(ingresos, gastos, saldo);
    }
}
