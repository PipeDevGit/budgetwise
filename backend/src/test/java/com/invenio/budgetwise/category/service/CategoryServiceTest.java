package com.invenio.budgetwise.category.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.invenio.budgetwise.auth.domain.User;
import com.invenio.budgetwise.auth.repository.UserRepository;
import com.invenio.budgetwise.category.domain.Category;
import com.invenio.budgetwise.category.dto.CategoryRequest;
import com.invenio.budgetwise.category.dto.CategoryResponse;
import com.invenio.budgetwise.category.repository.CategoryRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

class CategoryServiceTest {

    private CategoryRepository categoryRepository;
    private CategoryService categoryService;
    private User ana;

    @BeforeEach
    void setUp() {
        categoryRepository = mock(CategoryRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        categoryService = new CategoryService(categoryRepository, userRepository);

        ana = mock(User.class);
        when(ana.getId()).thenReturn(1L);
        when(userRepository.findByEmail("ana@example.com")).thenReturn(Optional.of(ana));
    }

    @Test
    void listarDevuelvePrimeroLasPredefinidasYDespuesLasPropias() {
        when(categoryRepository.findDisponiblesPara(1L)).thenReturn(List.of(
                new Category("Mascotas", ana),
                Category.predefinida("Transporte"),
                Category.predefinida("Comida")));

        List<CategoryResponse> categorias = categoryService.listar("ana@example.com");

        assertThat(categorias).extracting(CategoryResponse::name)
                .containsExactly("Comida", "Transporte", "Mascotas");
        assertThat(categorias).extracting(CategoryResponse::predefinida)
                .containsExactly(true, true, false);
    }

    @Test
    void crearGuardaLaCategoriaComoPropiaDelUsuario() {
        when(categoryRepository.findDisponiblesPara(1L)).thenReturn(List.of(Category.predefinida("Comida")));
        when(categoryRepository.save(any(Category.class))).thenAnswer(inv -> inv.getArgument(0));

        CategoryResponse creada = categoryService.crear("ana@example.com", new CategoryRequest("  Mascotas  "));

        assertThat(creada.name()).isEqualTo("Mascotas");
        assertThat(creada.predefinida()).isFalse();
    }

    @Test
    void crearConElNombreDeUnaQueYaExisteLanza409SinImportarMayusculas() {
        when(categoryRepository.findDisponiblesPara(1L)).thenReturn(List.of(Category.predefinida("Comida")));

        assertThatThrownBy(() -> categoryService.crear("ana@example.com", new CategoryRequest("COMIDA")))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("409")
                .hasMessageContaining("Ya existe una categoria con ese nombre");
        verify(categoryRepository, never()).save(any(Category.class));
    }
}
