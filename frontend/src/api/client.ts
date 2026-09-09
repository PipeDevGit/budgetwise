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

/** Lo que devuelven /auth/register y /auth/login segun la issue #4. */
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
  return pedir<RespuestaAuth>('/auth/register', {
    method: 'POST',
    body: JSON.stringify(datos),
  })
}

export function iniciarSesion(datos: Credenciales): Promise<RespuestaAuth> {
  return pedir<RespuestaAuth>('/auth/login', {
    method: 'POST',
    body: JSON.stringify(datos),
  })
}
