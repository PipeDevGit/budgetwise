package com.invenio.budgetwise.transaction.domain;

import com.invenio.budgetwise.auth.domain.User;
import com.invenio.budgetwise.category.domain.Category;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * BigDecimal y no double: con dinero, la aritmetica de punto flotante
     * acumula errores de redondeo. precision 12 / scale 2 alcanza para el MVP.
     */
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TransactionType type;

    @Column(nullable = false)
    private LocalDate date;

    @Column(length = 255)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    protected Transaction() {
        // Requerido por JPA.
    }

    public Transaction(BigDecimal amount, TransactionType type, LocalDate date,
            String description, User user, Category category) {
        this.amount = amount;
        this.type = type;
        this.date = date;
        this.description = description;
        this.user = user;
        this.category = category;
    }

    /** Editar un movimiento (issue #7): el dueno (user) no cambia, todo lo demas si. */
    public void actualizar(BigDecimal amount, TransactionType type, LocalDate date,
            String description, Category category) {
        this.amount = amount;
        this.type = type;
        this.date = date;
        this.description = description;
        this.category = category;
    }

    public Long getId() {
        return id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public TransactionType getType() {
        return type;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public User getUser() {
        return user;
    }

    public Category getCategory() {
        return category;
    }
}
