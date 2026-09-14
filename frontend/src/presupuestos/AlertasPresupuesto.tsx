import type { EstadoPresupuesto } from '../api/client'
import { formatearMonto } from '../transacciones/montos'
import { presupuestosExcedidos } from './alertas'

export function AlertasPresupuesto({ presupuestos }: { presupuestos: EstadoPresupuesto[] }) {
  const excedidos = presupuestosExcedidos(presupuestos)

  if (excedidos.length === 0) {
    return null
  }

  return (
    <div role="alert" className="alerta-presupuesto">
      <strong>Te pasaste del presupuesto del mes</strong>
      <ul>
        {excedidos.map((excedido) => (
          <li key={excedido.id}>
            {excedido.categoryName}: gastaste {formatearMonto(excedido.spent)} de{' '}
            {formatearMonto(excedido.monthlyLimit)}
          </li>
        ))}
      </ul>
    </div>
  )
}
