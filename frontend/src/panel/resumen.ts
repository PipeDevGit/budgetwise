import type { Transaccion } from '../api/client'

/** Un punto del grafico: cuanto se gasto en una categoria. */
export type GastoCategoria = {
  categoria: string
  monto: number
}

/**
 * "AAAA-MM" de una fecha, en hora local. Las fechas de la API llegan como
 * "AAAA-MM-DD", asi que comparar el prefijo alcanza para saber si un
 * movimiento es de ese mes, sin convertir a Date ni pelear con zonas horarias.
 */
export function mesDe(fecha: Date): string {
  const mes = String(fecha.getMonth() + 1).padStart(2, '0')
  return `${fecha.getFullYear()}-${mes}`
}

function gastosDelMes(transacciones: Transaccion[], mes: string): Transaccion[] {
  return transacciones.filter((movimiento) => movimiento.type === 'GASTO' && movimiento.date.startsWith(mes))
}

/** Total gastado en el mes indicado ("AAAA-MM"). Los ingresos no cuentan. */
export function gastoDelMes(transacciones: Transaccion[], mes: string): number {
  return gastosDelMes(transacciones, mes).reduce((total, movimiento) => total + movimiento.amount, 0)
}

/** Gasto del mes agrupado por categoria, de mayor a menor, listo para el grafico. */
export function gastoPorCategoria(transacciones: Transaccion[], mes: string): GastoCategoria[] {
  const totales = new Map<string, number>()
  for (const movimiento of gastosDelMes(transacciones, mes)) {
    totales.set(movimiento.categoryName, (totales.get(movimiento.categoryName) ?? 0) + movimiento.amount)
  }
  return [...totales]
    .map(([categoria, monto]) => ({ categoria, monto }))
    .sort((a, b) => b.monto - a.monto)
}
