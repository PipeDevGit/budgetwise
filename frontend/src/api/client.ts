import { leerToken } from '../auth/session'

const API_URL = import.meta.env.VITE_API_URL ?? 'http://localhost:8080'

export type HealthResponse = {
  status: string
  application: string
}

export type Credenciales = {
  email: string
  password: string
}

export type DatosRegistro = Credenciales & {
  name: string
}

export type TipoTransaccion = 'INGRESO' | 'GASTO'

/** Un movimiento, tal como lo devuelve la API de la issue #7. */
export type Transaccion = {
  id: number
  amount: number
  type: TipoTransaccion
  date: string
  description: string | null
  categoryId: number
  categoryName: string
}

/** Lo que espera POST /api/transactions. El monto viaja siempre positivo. */
export type NuevaTransaccion = {
  amount: number
  type: TipoTransaccion
  date: string
  categoryId: number
  description: string | null
}

/** Contrato pedido a @yariel3199-gif en la issue #8; todavia no existe. */
export type Categoria = {
  id: number
  name: string
  predefinida: boolean
}

/** Lo que devuelven /api/auth/register y /api/auth/login (issue #4). */
export type RespuestaAuth = {
  token: string
}

/**
 * Unico punto por donde sale una peticion al backend. Centraliza tres cosas
 * que si no se repetirian en cada pantalla: la URL base, el header
 * Authorization con el JWT, y la lectura del mensaje de error.
 */
async function pedir<T>(ruta: string, opciones: RequestInit = {}): Promise<T> {
  const token = leerToken()

  let respuesta: Response
  try {
    respuesta = await fetch(`${API_URL}${ruta}`, {
      ...opciones,
      headers: {
        'Content-Type': 'application/json',
        // Cuando hay sesion abierta, toda peticion viaja con el token. Los
        // endpoints protegidos que lo van a usar de verdad llegan con la #7.
        ...(token ? { Authorization: `Bearer ${token}` } : {}),
        ...opciones.headers,
      },
    })
  } catch {
    // fetch solo lanza si no se pudo llegar al servidor. Un 4xx o 5xx no
    // lanza: eso se revisa abajo con respuesta.ok.
    throw new Error('No se pudo conectar con la API. Verifica que el backend este corriendo.')
  }

  if (!respuesta.ok) {
    throw new Error(await mensajeDeError(respuesta))
  }

  // Un 204 no trae cuerpo: es lo que responde el DELETE de /api/transactions
  // (issue #7). Llamar a json() aca fallaria, porque no hay nada que parsear.
  if (respuesta.status === 204) {
    return undefined as T
  }

  return respuesta.json() as Promise<T>
}

/** Spring devuelve un JSON con "message"; si no lo hay, queda el codigo. */
async function mensajeDeError(respuesta: Response): Promise<string> {
  try {
    const cuerpo = await respuesta.json()
    if (cuerpo && typeof cuerpo.message === 'string') {
      return cuerpo.message
    }
  } catch {
    // El cuerpo no era JSON; nos quedamos con el codigo de estado.
  }
  return `La API respondio ${respuesta.status}`
}

export function getHealth(): Promise<HealthResponse> {
  return pedir<HealthResponse>('/health')
}

export function registrar(datos: DatosRegistro): Promise<RespuestaAuth> {
  return pedir<RespuestaAuth>('/api/auth/register', {
    method: 'POST',
    body: JSON.stringify(datos),
  })
}

export function iniciarSesion(datos: Credenciales): Promise<RespuestaAuth> {
  return pedir<RespuestaAuth>('/api/auth/login', {
    method: 'POST',
    body: JSON.stringify(datos),
  })
}

export function listarTransacciones(): Promise<Transaccion[]> {
  return pedir<Transaccion[]>('/api/transactions')
}

export function crearTransaccion(datos: NuevaTransaccion): Promise<Transaccion> {
  return pedir<Transaccion>('/api/transactions', {
    method: 'POST',
    body: JSON.stringify(datos),
  })
}

// El endpoint todavia no existe: pedido en la issue #8. Hasta que entre,
// la pantalla muestra la lista y el saldo, pero no deja agregar.
export function listarCategorias(): Promise<Categoria[]> {
  return pedir<Categoria[]>('/api/categories')
}
