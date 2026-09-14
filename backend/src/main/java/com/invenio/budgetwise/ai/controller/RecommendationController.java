package com.invenio.budgetwise.ai.controller;

import com.invenio.budgetwise.ai.dto.RecommendationResponse;
import com.invenio.budgetwise.ai.service.RecommendationService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * GET /api/ai/recommendations (issue #16). D-03 dice /ai/recommendations; va
 * bajo /api como el resto de la API. El controller solo delega.
 */
@RestController
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping("/api/ai/recommendations")
    public RecommendationResponse recomendaciones(Authentication authentication) {
        return recommendationService.recomendar(authentication.getName());
    }
}
