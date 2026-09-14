import type { Meta } from '../api/client'

/**
 * La API redondea el porcentaje hacia abajo y lo topa en 100, asi que solo
 * llega a 100 cuando lo ahorrado alcanza el objetivo: con 299 999 de 300 000
 * dice 99, no 100. Por eso alcanza con mirar el porcentaje, sin comparar
 * montos en el frontend.
 */
export function metaCumplida(meta: Meta): boolean {
  return meta.progressPercent >= 100
}
