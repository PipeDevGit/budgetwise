import { Bar, BarChart, CartesianGrid, ResponsiveContainer, Tooltip, XAxis, YAxis } from 'recharts'
import { formatearMonto } from '../transacciones/montos'
import type { GastoCategoria } from './resumen'

/**
 * Grafico de barras de Recharts, usado tal como viene la libreria (criterio de
 * la issue #11): solo componentes de Recharts, sin nada propio encima.
 */
export function GraficoGastosPorCategoria({ datos }: { datos: GastoCategoria[] }) {
  return (
    <ResponsiveContainer width="100%" height={280}>
      <BarChart data={datos} margin={{ top: 8, right: 8, bottom: 8, left: 8 }}>
        <CartesianGrid strokeDasharray="3 3" vertical={false} />
        <XAxis dataKey="categoria" />
        <YAxis tickFormatter={(valor: number) => formatearMonto(valor)} width={100} />
        <Tooltip formatter={(valor) => formatearMonto(Number(valor))} />
        <Bar dataKey="monto" name="Gasto" fill="#b3261e" />
      </BarChart>
    </ResponsiveContainer>
  )
}
