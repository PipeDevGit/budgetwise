import { describe, expect, it } from 'vitest'
import type { RespuestaRecomendaciones } from '../api/client'
import { cargarRecomendaciones } from './cargarRecomendaciones'

const RESPUESTA: RespuestaRecomendaciones = {
  recommendations: ['Uno', 'Dos', 'Tres'],
  source: 'gemini',
}

describe('cargarRecomendaciones', () => {
  it('devuelve las recomendaciones listas para mostrar cuando la API responde', async () => {
    const resultado = await cargarRecomendaciones(async () => RESPUESTA)

    expect(resultado).toEqual({ estado: 'listo', datos: RESPUESTA })
  })

  it('convierte un fallo de la API en el mensaje que ve el usuario', async () => {
    const resultado = await cargarRecomendaciones(async () => {
      throw new Error('No se pudo conectar con la API')
    })

    expect(resultado).toEqual({ estado: 'error', mensaje: 'No se pudo conectar con la API' })
  })

  it('trata una lista vacia como error, para no mostrar una seccion sin consejos', async () => {
    const resultado = await cargarRecomendaciones(async () => ({ recommendations: [], source: 'reglas' }))

    expect(resultado.estado).toBe('error')
  })
})
