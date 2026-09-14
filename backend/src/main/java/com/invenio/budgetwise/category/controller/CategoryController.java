package com.invenio.budgetwise.category.controller;

import com.invenio.budgetwise.category.dto.CategoryRequest;
import com.invenio.budgetwise.category.dto.CategoryResponse;
import com.invenio.budgetwise.category.service.CategoryService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Categorias (issue #8). Solo recibe la peticion y delega en CategoryService. */
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public List<CategoryResponse> listar(Authentication authentication) {
        return categoryService.listar(authentication.getName());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponse crear(Authentication authentication, @Valid @RequestBody CategoryRequest request) {
        return categoryService.crear(authentication.getName(), request);
    }
}
