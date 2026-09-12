package com.invenio.budgetwise.transaction.controller;

import com.invenio.budgetwise.transaction.dto.TransactionRequest;
import com.invenio.budgetwise.transaction.dto.TransactionResponse;
import com.invenio.budgetwise.transaction.service.TransactionService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * CRUD de ingresos y gastos (issue #7). El controller solo recibe la
 * peticion y el email del usuario autenticado; toda la logica (incluida la
 * de "cada usuario ve solo lo suyo") vive en TransactionService.
 */
@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    public List<TransactionResponse> listar(Authentication authentication) {
        return transactionService.listar(authentication.getName());
    }

    /** No la pide la issue #7 al pie de la letra, pero el frontend la necesita para precargar el formulario de edicion antes de un PUT. */
    @GetMapping("/{id}")
    public TransactionResponse obtener(Authentication authentication, @PathVariable Long id) {
        return transactionService.obtener(authentication.getName(), id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse crear(Authentication authentication, @Valid @RequestBody TransactionRequest request) {
        return transactionService.crear(authentication.getName(), request);
    }

    @PutMapping("/{id}")
    public TransactionResponse actualizar(
            Authentication authentication, @PathVariable Long id, @Valid @RequestBody TransactionRequest request) {
        return transactionService.actualizar(authentication.getName(), id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(Authentication authentication, @PathVariable Long id) {
        transactionService.eliminar(authentication.getName(), id);
    }
}
