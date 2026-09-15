import { useEffect, useState } from 'react'
import { listarTransacciones, obtenerSaldo, type Saldo, type Transaccion } from '../api/client'
import { formatearMonto } from '../transacciones/montos'
import { GraficoGastosPorCategoria } from './GraficoGastosPorCategoria'
import { Recomendaciones } from './Recomendaciones'
import { gastoDelMes, gastoPorCategoria, mesDe } from './resumen'

type Props = {
  onCerrarSesion: () => void
}

/**
 * Panel de control financiero (issue #11): saldo actual, gasto del mes y el
 * grafico de gastos por categoria. Abajo, las recomendaciones del mes (#17).
 *
 * El saldo viene del servidor (GET /api/balance, issue #15), como anticipaba
 * el comentario de montos.ts. El gasto del mes y el grafico se calculan sobre
 * la lista de movimientos, que ya trae la categoria de cada uno.
 */
export function PanelControl({ onCerrarSesion }: Props) {
  const [saldo, setSaldo] = useState<Saldo | null>(null)
  const [transacciones, setTransacciones] = useState<Transaccion[]>([])
  const [cargando, setCargando] = useState(true)
  const [errorSaldo, setErrorSaldo] = useState<string | null>(null)
  const [errorMovimientos, setErrorMovimientos] = useState<string | null>(null)

  useEffect(() => {
    // Mismo criterio que la pantalla de transacciones: si una de las dos
    // peticiones falla, lo que trajo la otra se sigue mostrando.
    async function cargar() {
      const [resultadoSaldo, resultadoMovimientos] = await Promise.allSettled([
        obtenerSaldo(),
        listarTransacciones(),
      ])

      if (resultadoSaldo.status === 'fulfilled') {
        setSaldo(resultadoSaldo.value)
      } else {
        setErrorSaldo((resultadoSaldo.reason as Error).message)
      }

      if (resultadoMovimientos.status === 'fulfilled') {
        setTransacciones(resultadoMovimientos.value)
      } else {
        setErrorMovimientos((resultadoMovimientos.reason as Error).message)
      }

      setCargando(false)
    }

    void cargar()
  }, [])

  const mes = mesDe(new Date())
  const porCategoria = gastoPorCategoria(transacciones, mes)

  return (
    <section>
      <header className="cabecera">
        <div>
          <p className="etiqueta">Saldo actual</p>
          {saldo && (
            <p className={saldo.balance < 0 ? 'saldo gasto' : 'saldo'}>{formatearMonto(saldo.balance)}</p>
          )}
        </div>
        <button type="button" onClick={onCerrarSesion}>
          Cerrar sesion
        </button>
      </header>

      {cargando && <p>Cargando el panel…</p>}

      {errorSaldo && (
        <p role="alert" className="error">
          No se pudo cargar el saldo: {errorSaldo}
        </p>
      )}

      {errorMovimientos && (
        <p role="alert" className="error">
          No se pudieron cargar los movimientos: {errorMovimientos}
        </p>
      )}

      {!cargando && !errorMovimientos && (
        <>
          <div className="tarjeta">
            <p className="etiqueta">Gasto del mes</p>
            <p className="monto-tarjeta gasto">{formatearMonto(gastoDelMes(transacciones, mes))}</p>
          </div>

          <h3>Gastos del mes por categoria</h3>
          {porCategoria.length === 0 ? (
            <p>Todavia no hay gastos este mes.</p>
          ) : (
            <GraficoGastosPorCategoria datos={porCategoria} />
          )}
        </>
      )}

      {/* Fuera del bloque de arriba: se ven aunque falle la carga de movimientos. */}
      <Recomendaciones />
    </section>
  )
}
