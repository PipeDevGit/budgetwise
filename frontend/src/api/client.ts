const API_URL = import.meta.env.VITE_API_URL ?? 'http://localhost:8080'

export type HealthResponse = {
  status: string
  application: string
}

/**
 * Cliente HTTP de la API. Todo el acceso al backend pasa por aca, para no
 * repetir la URL base ni el manejo de errores en cada pantalla.
 */
export async function getHealth(): Promise<HealthResponse> {
  const response = await fetch(`${API_URL}/health`)
  if (!response.ok) {
    throw new Error(`La API respondio ${response.status}`)
  }
  return response.json()
}
