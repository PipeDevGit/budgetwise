const CLAVE_TOKEN = 'budgetwise.token'

/**
 * El JWT vive en localStorage para que la sesion sobreviva a recargar la
 * pagina. Es lo mas simple que cumple el criterio de la issue #5, y deja el
 * almacenamiento en un solo lugar: si mas adelante hay que cambiarlo, se
 * cambia aca y no en cada pantalla.
 */
export function guardarToken(token: string): void {
  localStorage.setItem(CLAVE_TOKEN, token)
}

export function leerToken(): string | null {
  return localStorage.getItem(CLAVE_TOKEN)
}

export function borrarToken(): void {
  localStorage.removeItem(CLAVE_TOKEN)
}

type Oyente = () => void

const oyentesDeSesionVencida = new Set<Oyente>()

/**
 * Se llama cuando la API rechaza el token guardado: vencio, se firmo con otro
 * secreto o el usuario ya no existe en la base. Sin esto, la aplicacion seguia
 * mostrando la sesion abierta con saldo 0 y un error 403 en cada seccion.
 */
export function avisarSesionVencida(): void {
  borrarToken()
  oyentesDeSesionVencida.forEach((oyente) => oyente())
}

/** App se suscribe para volver al login. Devuelve la funcion para desuscribirse. */
export function alVencerLaSesion(oyente: Oyente): () => void {
  oyentesDeSesionVencida.add(oyente)
  return () => {
    oyentesDeSesionVencida.delete(oyente)
  }
}
