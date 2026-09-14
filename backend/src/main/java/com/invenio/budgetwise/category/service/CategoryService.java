package com.invenio.budgetwise.category.service;

import com.invenio.budgetwise.auth.domain.User;
import com.invenio.budgetwise.auth.repository.UserRepository;
import com.invenio.budgetwise.category.domain.Category;
import com.invenio.budgetwise.category.dto.CategoryRequest;
import com.invenio.budgetwise.category.dto.CategoryResponse;
import com.invenio.budgetwise.category.repository.CategoryRepository;
import java.util.Comparator;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * Categorias de la issue #8: las precargadas que ve todo el mundo mas las que
 * cada usuario crea para si mismo. Nadie ve las categorias propias de otro.
 */
@Service
public class CategoryService {

    /** Primero las de todos, despues las propias; dentro de cada grupo, por nombre. */
    private static final Comparator<Category> PREDEFINIDAS_PRIMERO = Comparator
            .comparing((Category categoria) -> !categoria.esPredefinida())
            .thenComparing(Category::getName, String.CASE_INSENSITIVE_ORDER);

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public CategoryService(CategoryRepository categoryRepository, UserRepository userRepository) {
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> listar(String email) {
        Long userId = usuarioAutenticado(email).getId();
        return categoryRepository.findDisponiblesPara(userId).stream()
                .sorted(PREDEFINIDAS_PRIMERO)
                .map(CategoryService::aRespuesta)
                .toList();
    }

    /**
     * No se permite repetir un nombre que el usuario ya ve, sin importar
     * mayusculas: tener Comida y comida en el mismo formulario solo confunde.
     */
    @Transactional
    public CategoryResponse crear(String email, CategoryRequest request) {
        User user = usuarioAutenticado(email);
        String nombre = request.name().trim();
        boolean repetida = categoryRepository.findDisponiblesPara(user.getId()).stream()
                .anyMatch(existente -> existente.getName().equalsIgnoreCase(nombre));
        if (repetida) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe una categoria con ese nombre");
        }
        return aRespuesta(categoryRepository.save(new Category(nombre, user)));
    }

    private User usuarioAutenticado(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    }

    private static CategoryResponse aRespuesta(Category categoria) {
        return new CategoryResponse(categoria.getId(), categoria.getName(), categoria.esPredefinida());
    }
}
