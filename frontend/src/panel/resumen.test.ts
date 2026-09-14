import { describe, expect, it } from 'vitest'
import type { TipoTransaccion, Transaccion } from '../api/client'
import { gastoDelMes, gastoPorCategoria, mesDe } from './resumen'

function movimiento(type: TipoTransaccion, amount: number, date: string, categoryName: string): Transaccion {
  return { id: 1, amount, type, date, description: null, categoryId: 1, categoryName }
}

const MOVIMIENTOS: Transaccion[] = [
  movimiento('INGRESO', 400000, '2026-09-01', 'Otros'),
  movimiento('GASTO', 30000, '2026-09-05', 'Comida'),
  movimiento('GASTO', 25000, '2026-09-10', 'Comida'),
  movimiento('GASTO', 12000, '2026-09-11', 'Transporte'),
  movimiento('GASTO', 99000, '2026-08-28', 'Ocio'),
]

describe('mesDe', () => {
  it('arma AAAA-MM con el mes en dos digitos', () => {
    // En Date los meses empiezan en 0: el 8 es septiembre.
    expect(mesDe(new Date(2026, 8, 13))).toBe('2026-09')
  })
})

describe('gastoDelMes', () => {
  it('suma solo los gastos del mes: ni los ingresos ni los gastos de otro mes', () => {
    expect(gastoDelMes(MOVIMIENTOS, '2026-09')).toBe(67000)
  })

  it('es cero si ese mes no hubo gastos', () => {
    expect(gastoDelMes(MOVIMIENTOS, '2026-07')).toBe(0)
  })
})

describe('gastoPorCategoria', () => {
  it('agrupa los gastos del mes por categoria, de mayor a menor', () => {
    expect(gastoPorCategoria(MOVIMIENTOS, '2026-09')).toEqual([
      { categoria: 'Comida', monto: 55000 },
      { categoria: 'Transporte', monto: 12000 },
    ])
  })

  it('no incluye una categoria que solo tuvo gastos en otro mes', () => {
    expect(gastoPorCategoria(MOVIMIENTOS, '2026-09').map((gasto) => gasto.categoria)).not.toContain('Ocio')
  })
})
