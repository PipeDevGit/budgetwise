import type { Transaccion } from '../api/client'
import { formatearMonto } from './montos'

export function ListaTransacciones({ transacciones }: { transacciones: Transaccion[] }) {
  if (transacciones.length === 0) {
    return <p>Todavia no hay movimientos. Agrega el primero con el formulario de arriba.</p>
  }

  return (
    <table>
      <thead>
        <tr>
          <th>Fecha</th>
          <th>Descripcion</th>
          <th>Categoria</th>
          <th className="derecha">Monto</th>
        </tr>
      </thead>
      <tbody>
        {transacciones.map((movimiento) => (
          <tr key={movimiento.id}>
            <td>{movimiento.date}</td>
            <td>{movimiento.description ?? '—'}</td>
            <td>{movimiento.categoryName}</td>
            <td className={movimiento.type === 'INGRESO' ? 'derecha ingreso' : 'derecha gasto'}>
              {movimiento.type === 'INGRESO' ? '+' : '−'} {formatearMonto(movimiento.amount)}
            </td>
          </tr>
        ))}
      </tbody>
    </table>
  )
}
