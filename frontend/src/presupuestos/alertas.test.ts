import { describe, expect, it } from 'vitest'
import type { EstadoPresupuesto } from '../api/client'
import { presupuestosExcedidos } from './alertas'

function presupuesto(categoryName: string, spent: number, monthlyLimit: number, exceeded: boolean): EstadoPresupuesto {
  return { id: 1, categoryId: 1, categoryName, period: '2026-09', monthlyLimit, spent, exceeded }
}

describe('presupuestosExcedidos', () => {
  it('alerta solo los presupuestos que la API marca como excedidos', () => {
    const comida = presupuesto('Comida', 92500.5, 80000, true)
    const ocio = presupuesto('Ocio', 5000, 30000, false)

    expect(presupuestosExcedidos([comida, ocio])).toEqual([comida])
  })

  // El caso limite de la #13: gastar exactamente el presupuesto no es pasarse.
  it('gastar exactamente el limite no es una alerta', () => {
    expect(presupuestosExcedidos([presupuesto('Comida', 80000, 80000, false)])).toEqual([])
  })

  // Esta prueba fija donde vive la regla: si alguien la cambia por comparar
  // spent contra monthlyLimit en el frontend, falla.
  it('no compara montos: obedece la marca de la API', () => {
    const marcado = presupuesto('Salud', 10, 20, true)
    const noMarcado = presupuesto('Ocio', 50, 20, false)

    expect(presupuestosExcedidos([marcado, noMarcado])).toEqual([marcado])
  })
})
