import { useCallback, useEffect, useState } from 'react'
import { listarCategorias, listarTransacciones, type Categoria, type Transaccion } from '../api/client'
import { FormularioTransaccion } from './FormularioTransaccion'
import { ListaTransacciones } from './ListaTransacciones'
import { calcularSaldo, formatearMonto } from './montos'

type Props = {
  onCerrarSesion: () => void
}

export function PantallaTransacciones({ onCerrarSesion }: Props) {
  const [transacciones, setTransacciones] = useState<Transaccion[]>([])
  const [categorias, setCategorias] = useState<Categoria[]>([])
  const [cargando, setCargando] = useState(true)
  const [errorMovimientos, setErrorMovimientos] = useState<string | null>(null)
  const [errorCategorias, setErrorCategorias] = useState<string | null>(null)

  /**
   * Las dos peticiones salen a la vez pero se manejan por separado con
   * allSettled: si el endpoint de categorias falla, la lista y el saldo
   * tienen que seguir viendose. Hoy eso pasa de verdad, porque
   * GET /api/categories todavia no existe (pedido en la issue #8).
   */
  const cargar = useCallback(async () => {
    setErrorMovimientos(null)
    setErrorCategorias(null)

    const [movimientos, cats] = await Promise.allSettled([listarTransacciones(), listarCategorias()])

    if (movimientos.status === 'fulfilled') {
      setTransacciones(movimientos.value)
    } else {
      setErrorMovimientos((movimientos.reason as Error).message)
    }

    if (cats.status === 'fulfilled') {
      setCategorias(cats.value)
    } else {
      setErrorCategorias((cats.reason as Error).message)
    }

    setCargando(false)
  }, [])

  useEffect(() => {
    void cargar()
  }, [cargar])

  const saldo = calcularSaldo(transacciones)

  return (
    <section>
      <header className="cabecera">
        <div>
          <p className="etiqueta">Saldo disponible</p>
          <p className={saldo < 0 ? 'saldo gasto' : 'saldo'}>{formatearMonto(saldo)}</p>
        </div>
        <button type="button" onClick={onCerrarSesion}>
          Cerrar sesion
        </button>
      </header>

      {cargando && <p>Cargando movimientos…</p>}

      {errorMovimientos && (
        <p role="alert" className="error">
          No se pudieron cargar los movimientos: {errorMovimientos}
        </p>
      )}

      {!cargando && errorCategorias && (
        <p role="alert" className="error">
          No se pudieron cargar las categorias, asi que no se pueden agregar movimientos todavia:{' '}
          {errorCategorias}
        </p>
      )}

      {!cargando && categorias.length > 0 && (
        <FormularioTransaccion categorias={categorias} onCreada={cargar} />
      )}

      <h3>Movimientos</h3>
      {!cargando && !errorMovimientos && <ListaTransacciones transacciones={transacciones} />}
    </section>
  )
}
