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
