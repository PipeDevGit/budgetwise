import { describe, expect, it } from 'vitest'
import { describirFuente } from './fuenteRecomendaciones'

describe('describirFuente', () => {
  it('dice que las genero la IA cuando respondio Gemini', () => {
    expect(describirFuente('gemini')).toBe('Generadas con IA (Gemini)')
  })

  it('aclara que respondieron las reglas cuando la IA no estuvo disponible', () => {
    expect(describirFuente('reglas')).toContain('reglas')
  })

  it('no muestra nada si la fuente es desconocida', () => {
    expect(describirFuente('otra')).toBe('')
  })
})
