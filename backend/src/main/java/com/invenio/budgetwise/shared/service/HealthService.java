package com.invenio.budgetwise.shared.service;

import com.invenio.budgetwise.shared.dto.HealthResponse;
import org.springframework.stereotype.Service;

/**
 * La logica vive en el service y no en el controller, como exige CLAUDE.md.
 * Con un health check el beneficio es minimo, pero deja establecido el patron
 * para los calculos que si importan: saldo, alertas y progreso de metas.
 */
@Service
public class HealthService {

    public HealthResponse check() {
        return new HealthResponse("ok", "budgetwise");
    }
}
