package com.invenio.budgetwise.category.domain;

import com.invenio.budgetwise.auth.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String name;

    /**
     * Las categorias precargadas (comida, transporte, salud, ocio, otros) tienen
     * owner nulo y las ve todo el mundo. Una categoria creada por alguien lleva
     * su usuario y solo la ve esa persona.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    protected Category() {
        // Requerido por JPA.
    }

    public Category(String name, User owner) {
        this.name = name;
        this.owner = owner;
    }

    public static Category predefinida(String name) {
        return new Category(name, null);
    }

    public boolean esPredefinida() {
        return owner == null;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public User getOwner() {
        return owner;
    }
}
