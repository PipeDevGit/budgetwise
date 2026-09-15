/**
 * Texto que acompana los consejos, para que en la demo se vea quien los genero
 * (D-03 y D-12): el modelo de IA o el respaldo por reglas.
 */
export function describirFuente(source: string): string {
  if (source === 'gemini') {
    return 'Generadas con IA (Gemini)'
  }
  if (source === 'reglas') {
    return 'Generadas con reglas: la IA no respondio o no esta configurada'
  }
  return ''
}
