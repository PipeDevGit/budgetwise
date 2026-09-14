import type { Transaccion } from '../api/client'

/**
 * El saldo suma los ingresos y resta los gastos. El monto siempre viaja
 * positivo desde la API (issue #7): el signo lo da el tipo, no el numero.
 *
 * Se calcula en el cliente sobre la lista completa, que es la que ya se pidio
 * para mostrar la tabla. Cuando exista GET /balance (issue #15), el panel de
 * la #11 va a usar el saldo del servidor; para esta pantalla alcanza con esto
 * y evita una segunda peticion.
 */
export function calcularSaldo(transacciones: Transaccion[]): number {
  return transacciones.reduce(
    (saldo, movimiento) =>
      movimiento.type === 'INGRESO' ? saldo + movimiento.amount : saldo - movimiento.amount,
    0,
  )
}

/**
 * Dos decimales y separadores locales, sin simbolo de moneda: ni el enunciado
 * ni la API definen una, asi que no se inventa.
 */
export function formatearMonto(monto: number): string {
  return monto.toLocaleString('es-CR', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  })
}
