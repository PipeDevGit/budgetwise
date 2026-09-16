import type { EstadoPresupuesto } from '../api/client'

/**
 * Los presupuestos que hay que alertar. Se decide solo con exceeded, que
 * calcula la API: el caso limite (gastar exactamente el presupuesto no es
 * pasarse) ya esta resuelto y probado en BudgetService, y comparar montos
 * aca seria tener la misma regla en dos lugares.
 */
export function presupuestosExcedidos(presupuestos: EstadoPresupuesto[]): EstadoPresupuesto[] {
  return presupuestos.filter((presupuesto) => presupuesto.exceeded)
}
