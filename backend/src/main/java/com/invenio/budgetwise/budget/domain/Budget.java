package com.invenio.budgetwise.budget.domain;

import com.invenio.budgetwise.auth.domain.User;
import com.invenio.budgetwise.category.domain.Category;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
import java.time.YearMonth;

/**
 * Presupuesto mensual por categoria (issue #13). La evaluacion del sobrepaso
 * vive en el service, no aca ni en el controller.
 */
@Entity
@Table(
    name = "budgets",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_budget_usuario_categoria_mes",
        columnNames = {"user_id", "category_id", "period"}
    )
)
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "monthly_limit", nullable = false, precision = 12, scale = 2)
    private BigDecimal monthlyLimit;

    /**
     * Mes al que aplica, como texto ISO "2026-09". YearMonth no tiene un tipo
     * nativo en JDBC, y guardarlo asi lo deja legible y ordenable en la base.
     */
    @Column(nullable = false, length = 7)
    private String period;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    protected Budget() {
        // Requerido por JPA.
    }

    public Budget(BigDecimal monthlyLimit, YearMonth period, User user, Category category) {
        this.monthlyLimit = monthlyLimit;
        this.period = period.toString();
        this.user = user;
        this.category = category;
    }

    public Long getId() {
        return id;
    }

    public BigDecimal getMonthlyLimit() {
        return monthlyLimit;
    }

    public YearMonth getPeriod() {
        return YearMonth.parse(period);
    }

    public User getUser() {
        return user;
    }

    public Category getCategory() {
        return category;
    }
}
