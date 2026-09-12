import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'
import { guardarToken } from '../auth/session'
import { getHealth, iniciarSesion, registrar } from './client'

/** Vitest corre en Node, donde no existe localStorage: se reemplaza en memoria. */
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

/** Una respuesta HTTP como la que devolveria el backend. */
function respuesta(status: number, cuerpo?: unknown): Response {
  return new Response(cuerpo === undefined ? null : JSON.stringify(cuerpo), { status })
}

/** Reemplaza fetch por uno que no sale a la red y devuelve la respuesta dada. */
function simularFetch(res: Response) {
  const fetchFalso = vi.fn<typeof fetch>(async () => res)
  vi.stubGlobal('fetch', fetchFalso)
  return fetchFalso
}

/** Los headers con los que pedir() llamo a fetch. */
function headersEnviados(fetchFalso: ReturnType<typeof simularFetch>) {
  const [, opciones] = fetchFalso.mock.calls[0]
  return opciones?.headers as Record<string, string>
}

// pedir() no se exporta: se prueba a traves de las funciones que la usan.
describe('pedir()', () => {
  beforeEach(() => {
    vi.stubGlobal('localStorage', localStorageEnMemoria())
  })

  afterEach(() => {
    vi.unstubAllGlobals()
  })

  // Estas dos pruebas hubieran atrapado el bug del PR #40, cuando el
  // frontend apuntaba a /auth y la API vivia en /api/auth.
  it('iniciarSesion manda un POST a /api/auth/login con el cuerpo en JSON', async () => {
    const fetchFalso = simularFetch(respuesta(200, { token: 'abc' }))

    await iniciarSesion({ email: 'ana@invenio.ac.cr', password: 'clave-segura' })

    const [url, opciones] = fetchFalso.mock.calls[0]
    expect(String(url)).toMatch(/\/api\/auth\/login$/)
    expect(opciones?.method).toBe('POST')
    expect(headersEnviados(fetchFalso)['Content-Type']).toBe('application/json')
    expect(JSON.parse(String(opciones?.body))).toEqual({
      email: 'ana@invenio.ac.cr',
      password: 'clave-segura',
    })
  })

  it('registrar manda un POST a /api/auth/register', async () => {
    const fetchFalso = simularFetch(respuesta(201, { token: 'abc' }))

    await registrar({ name: 'Ana', email: 'ana@invenio.ac.cr', password: 'clave-segura' })

    const [url, opciones] = fetchFalso.mock.calls[0]
    expect(String(url)).toMatch(/\/api\/auth\/register$/)
    expect(opciones?.method).toBe('POST')
  })

  it('manda el header Authorization cuando hay sesion abierta', async () => {
    guardarToken('token-guardado')
    const fetchFalso = simularFetch(respuesta(200, { status: 'ok', application: 'budgetwise' }))

    await getHealth()

    expect(headersEnviados(fetchFalso).Authorization).toBe('Bearer token-guardado')
  })

  it('no manda el header Authorization cuando no hay sesion', async () => {
    const fetchFalso = simularFetch(respuesta(200, { status: 'ok', application: 'budgetwise' }))

    await getHealth()

    expect(headersEnviados(fetchFalso)).not.toHaveProperty('Authorization')
  })

  it('si el servidor no responde, avisa que no se pudo conectar', async () => {
    vi.stubGlobal(
      'fetch',
      vi.fn<typeof fetch>(async () => {
        throw new TypeError('Failed to fetch')
      }),
    )

    await expect(getHealth()).rejects.toThrow('No se pudo conectar con la API')
  })

  it('si el servidor responde con error y un message, muestra ese mensaje', async () => {
    simularFetch(respuesta(401, { message: 'Correo o contrasena incorrectos' }))

    await expect(
      iniciarSesion({ email: 'ana@invenio.ac.cr', password: 'clave-mala' }),
    ).rejects.toThrow('Correo o contrasena incorrectos')
  })

  // Es lo que pasa hoy con la API real: los errores llegan sin cuerpo.
  it('si el error viene con el cuerpo vacio, muestra el codigo de estado', async () => {
    simularFetch(respuesta(401))

    await expect(
      iniciarSesion({ email: 'ana@invenio.ac.cr', password: 'clave-mala' }),
    ).rejects.toThrow('La API respondio 401')
  })

  // El DELETE de /api/transactions (issue #7) responde 204 sin cuerpo:
  // pedir() no puede llamar a json() ahi porque no hay nada que parsear.
  it('no intenta leer el cuerpo cuando la respuesta es 204', async () => {
    simularFetch(respuesta(204))

    await expect(getHealth()).resolves.toBeUndefined()
  })
})
