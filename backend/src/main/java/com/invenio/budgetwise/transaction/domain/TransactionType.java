package com.invenio.budgetwise.transaction.domain;

/** Un movimiento suma al saldo o lo resta. No hay un tercer caso en el MVP. */
public enum TransactionType {
    INGRESO,
    GASTO
}
