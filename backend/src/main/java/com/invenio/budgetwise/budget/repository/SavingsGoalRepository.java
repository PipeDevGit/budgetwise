package com.invenio.budgetwise.budget.repository;

import com.invenio.budgetwise.budget.domain.SavingsGoal;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SavingsGoalRepository extends JpaRepository<SavingsGoal, Long> {

    List<SavingsGoal> findByUserId(Long userId);

    Optional<SavingsGoal> findByIdAndUserId(Long id, Long userId);
}
