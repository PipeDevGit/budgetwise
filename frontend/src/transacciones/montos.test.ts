import { describe, expect, it } from 'vitest'
import type { Transaccion, TipoTransaccion } from '../api/client'
import { calcularSaldo } from './montos'

function movimiento(type: TipoTransaccion, amount: number): Transaccion {
  return {
    id: 1,
    amount,
    type,
    date: '2026-09-12',
    description: null,
    categoryId: 1,
    categoryName: 'Comida',
  }
}

describe('calcularSaldo', () => {
  it('es cero cuando no hay movimientos', () => {
    expect(calcularSaldo([])).toBe(0)
  })

  it('suma los ingresos y resta los gastos', () => {
    const saldo = calcularSaldo([movimiento('INGRESO', 1000), movimiento('GASTO', 250)])

    expect(saldo).toBe(750)
  })

  // El monto viaja siempre positivo, asi que un gasto mayor que los ingresos
  // tiene que dar negativo y no cero.
  it('queda negativo si se gasta mas de lo que entra', () => {
    const saldo = calcularSaldo([movimiento('INGRESO', 100), movimiento('GASTO', 400)])

    expect(saldo).toBe(-300)
  })
})
