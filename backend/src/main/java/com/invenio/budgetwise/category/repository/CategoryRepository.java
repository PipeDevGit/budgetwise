package com.invenio.budgetwise.category.repository;

import com.invenio.budgetwise.category.domain.Category;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    /** Las predefinidas (sin dueño) mas las propias del usuario. */
    @Query("select c from Category c where c.owner is null or c.owner.id = :userId")
    List<Category> findDisponiblesPara(Long userId);

    Optional<Category> findByNameAndOwnerIsNull(String name);
}
