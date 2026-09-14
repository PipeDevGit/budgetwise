package com.invenio.budgetwise.budget.controller;

import com.invenio.budgetwise.budget.dto.BudgetRequest;
import com.invenio.budgetwise.budget.dto.BudgetStatusResponse;
import com.invenio.budgetwise.budget.service.BudgetService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Presupuestos y alertas del mes en curso (issue #13).
 *
 * PUT y no POST: definir dos veces el presupuesto de la misma categoria deja
 * uno solo con el ultimo limite. Repetir la peticion no crea duplicados.
 */
@RestController
@RequestMapping("/api/budgets")
public class BudgetController {

    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @GetMapping
    public List<BudgetStatusResponse> estadoDelMes(Authentication authentication) {
        return budgetService.estadoDelMes(authentication.getName());
    }

    @PutMapping
    public BudgetStatusResponse definir(Authentication authentication, @Valid @RequestBody BudgetRequest request) {
        return budgetService.definir(authentication.getName(), request);
    }
}
