import type { Transaccion } from '../api/client'
import { formatearMonto } from './montos'

type Props = {
  transacciones: Transaccion[]
  /** Lo que se muestra si no hay nada: no es lo mismo "no hay movimientos" que "no hay en esta categoria". */
  vacio: string
}

export function ListaTransacciones({ transacciones, vacio }: Props) {
  if (transacciones.length === 0) {
    return <p>{vacio}</p>
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
