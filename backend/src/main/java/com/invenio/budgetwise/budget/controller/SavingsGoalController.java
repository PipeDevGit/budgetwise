package com.invenio.budgetwise.budget.controller;

import com.invenio.budgetwise.budget.dto.SavingsGoalRequest;
import com.invenio.budgetwise.budget.dto.SavingsGoalResponse;
import com.invenio.budgetwise.budget.dto.SavingsUpdateRequest;
import com.invenio.budgetwise.budget.service.SavingsGoalService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Metas de ahorro (issue #12). Solo recibe la peticion y delega en SavingsGoalService. */
@RestController
@RequestMapping("/api/goals")
public class SavingsGoalController {

    private final SavingsGoalService savingsGoalService;

    public SavingsGoalController(SavingsGoalService savingsGoalService) {
        this.savingsGoalService = savingsGoalService;
    }

    @GetMapping
    public List<SavingsGoalResponse> listar(Authentication authentication) {
        return savingsGoalService.listar(authentication.getName());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SavingsGoalResponse crear(Authentication authentication, @Valid @RequestBody SavingsGoalRequest request) {
        return savingsGoalService.crear(authentication.getName(), request);
    }

    @PutMapping("/{id}/savings")
    public SavingsGoalResponse actualizarAhorro(
            Authentication authentication, @PathVariable Long id, @Valid @RequestBody SavingsUpdateRequest request) {
        return savingsGoalService.actualizarAhorro(authentication.getName(), id, request);
    }
}
