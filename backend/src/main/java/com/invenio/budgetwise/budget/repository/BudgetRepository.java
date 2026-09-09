package com.invenio.budgetwise.budget.repository;

import com.invenio.budgetwise.budget.domain.Budget;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BudgetRepository extends JpaRepository<Budget, Long> {

    List<Budget> findByUserIdAndPeriod(Long userId, String period);

    Optional<Budget> findByUserIdAndCategoryIdAndPeriod(Long userId, Long categoryId, String period);
}
