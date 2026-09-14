import { describe, expect, it } from 'vitest'
import type { Meta } from '../api/client'
import { metaCumplida } from './progreso'

function meta(savedAmount: number, targetAmount: number, progressPercent: number): Meta {
  return { id: 1, name: 'Viaje', targetAmount, savedAmount, targetDate: '2026-12-15', progressPercent }
}

describe('metaCumplida', () => {
  it('esta cumplida cuando la API dice 100', () => {
    expect(metaCumplida(meta(300000, 300000, 100))).toBe(true)
  })

  // El caso limite: por el redondeo hacia abajo, casi llegar no es llegar.
  it('con 299 999 de 300 000 la API dice 99 y no esta cumplida', () => {
    expect(metaCumplida(meta(299999, 300000, 99))).toBe(false)
  })

  // Fija donde vive la regla: si alguien la cambia por comparar montos
  // en el frontend, esta prueba falla.
  it('no compara montos: obedece el porcentaje de la API', () => {
    expect(metaCumplida(meta(300000, 300000, 99))).toBe(false)
  })
})
