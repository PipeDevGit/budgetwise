import type { Categoria, EstadoPresupuesto } from '../api/client'
import { formatearMonto } from '../transacciones/montos'
import { FormularioPresupuesto } from './FormularioPresupuesto'

type Props = {
  presupuestos: EstadoPresupuesto[]
  categorias: Categoria[]
  onDefinido: () => void
}

export function PresupuestosDelMes({ presupuestos, categorias, onDefinido }: Props) {
  return (
    <section className="presupuestos">
      <h3>Presupuestos del mes</h3>

      {presupuestos.length === 0 ? (
        <p>Todavia no definiste ningun presupuesto.</p>
      ) : (
        <table>
          <thead>
            <tr>
              <th>Categoria</th>
              <th className="derecha">Gastado</th>
              <th className="derecha">Limite</th>
            </tr>
          </thead>
          <tbody>
            {presupuestos.map((presupuesto) => (
              <tr key={presupuesto.id}>
                <td>{presupuesto.categoryName}</td>
                <td className={presupuesto.exceeded ? 'derecha gasto' : 'derecha'}>
                  {formatearMonto(presupuesto.spent)}
                </td>
                <td className="derecha">{formatearMonto(presupuesto.monthlyLimit)}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}

      <FormularioPresupuesto categorias={categorias} onDefinido={onDefinido} />
    </section>
  )
}
