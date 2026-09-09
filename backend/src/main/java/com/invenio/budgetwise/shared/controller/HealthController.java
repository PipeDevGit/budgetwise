package com.invenio.budgetwise.shared.controller;

import com.invenio.budgetwise.shared.dto.HealthResponse;
import com.invenio.budgetwise.shared.service.HealthService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/** El controller solo recibe la peticion y delega en el service. */
@RestController
public class HealthController {

    private final HealthService healthService;

    public HealthController(HealthService healthService) {
        this.healthService = healthService;
    }

    @GetMapping("/health")
    public HealthResponse health() {
        return healthService.check();
    }
}
