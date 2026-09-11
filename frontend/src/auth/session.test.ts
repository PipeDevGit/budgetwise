import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'
import { borrarToken, guardarToken, leerToken } from './session'

/**
 * Vitest corre en Node, donde no existe localStorage. Este reemplazo en
 * memoria implementa solo los tres metodos que usa session.ts.
 */
function localStorageEnMemoria() {
  const datos = new Map<string, string>()
  return {
    getItem: (clave: string) => datos.get(clave) ?? null,
    setItem: (clave: string, valor: string) => {
      datos.set(clave, valor)
    },
    removeItem: (clave: string) => {
      datos.delete(clave)
    },
  }
}

describe('session', () => {
  beforeEach(() => {
    vi.stubGlobal('localStorage', localStorageEnMemoria())
  })

  afterEach(() => {
    vi.unstubAllGlobals()
  })

  it('devuelve null si no hay ningun token guardado', () => {
    expect(leerToken()).toBeNull()
  })

  it('devuelve el mismo token que se guardo', () => {
    guardarToken('token-de-prueba')

    expect(leerToken()).toBe('token-de-prueba')
  })

  it('despues de borrar el token ya no hay sesion', () => {
    guardarToken('token-de-prueba')
    borrarToken()

    expect(leerToken()).toBeNull()
  })
})
