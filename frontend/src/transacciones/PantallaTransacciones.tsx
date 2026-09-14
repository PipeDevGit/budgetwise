import { useCallback, useEffect, useRef, useState } from 'react'
import {
  listarCategorias,
  listarPresupuestos,
  listarTransacciones,
  type Categoria,
  type EstadoPresupuesto,
  type Transaccion,
} from '../api/client'
import { FormularioCategoria } from '../categorias/FormularioCategoria'
import { AlertasPresupuesto } from '../presupuestos/AlertasPresupuesto'
import { PresupuestosDelMes } from '../presupuestos/PresupuestosDelMes'
import { FiltroCategoria } from './FiltroCategoria'
import { FormularioTransaccion } from './FormularioTransaccion'
import { ListaTransacciones } from './ListaTransacciones'
import { calcularSaldo, formatearMonto } from './montos'

type Props = {
  onCerrarSesion: () => void
}

/** La lista filtrada recuerda de que categoria es, para no mostrarla bajo otro filtro. */
type ListaFiltrada = {
  categoriaId: number
  movimientos: Transaccion[]
}

export function PantallaTransacciones({ onCerrarSesion }: Props) {
  const [transacciones, setTransacciones] = useState<Transaccion[]>([])
  const [filtradas, setFiltradas] = useState<ListaFiltrada | null>(null)
  const [categorias, setCategorias] = useState<Categoria[]>([])
  const [presupuestos, setPresupuestos] = useState<EstadoPresupuesto[]>([])
  const [filtro, setFiltro] = useState<number | null>(null)
  const [cargando, setCargando] = useState(true)
  const [errorMovimientos, setErrorMovimientos] = useState<string | null>(null)
  const [errorCategorias, setErrorCategorias] = useState<string | null>(null)
  const [errorPresupuestos, setErrorPresupuestos] = useState<string | null>(null)
  const ultimaCarga = useRef(0)

  /**
   * Las peticiones salen a la vez pero se manejan por separado con
   * allSettled: si una falla, lo demas se sigue viendo.
   *
   * La lista completa se pide siempre, porque el saldo de arriba es el total
   * y no el de la categoria filtrada. Si hay filtro, la lista filtrada se le
   * pide a la API (?categoryId=, PR #58) en vez de filtrar aca, para no
   * repetir en el frontend lo que ya hace y prueba el backend.
   *
   * Los presupuestos se vuelven a pedir en cada carga porque lo gastado cambia
   * con cada movimiento: asi la alerta aparece apenas se registra el gasto que
   * pasa el limite (issue #13).
   */
  const cargar = useCallback(async () => {
    // Si se cambia de filtro rapido, la respuesta de un filtro viejo puede
    // llegar despues que la del nuevo. Cada carga se numera y solo se aplica
    // la ultima que se pidio.
    const numero = ++ultimaCarga.current

    const [todas, cats, conFiltro, estados] = await Promise.allSettled([
      listarTransacciones(),
      listarCategorias(),
      filtro === null ? Promise.resolve<Transaccion[]>([]) : listarTransacciones(filtro),
      listarPresupuestos(),
    ])

    if (numero !== ultimaCarga.current) {
      return
    }

    setErrorMovimientos(null)
    setErrorCategorias(null)
    setErrorPresupuestos(null)

    if (todas.status === 'fulfilled') {
      setTransacciones(todas.value)
    } else {
      setErrorMovimientos((todas.reason as Error).message)
    }

    if (filtro !== null) {
      if (conFiltro.status === 'fulfilled') {
        setFiltradas({ categoriaId: filtro, movimientos: conFiltro.value })
      } else {
        setErrorMovimientos((conFiltro.reason as Error).message)
      }
    }

    if (cats.status === 'fulfilled') {
      setCategorias(cats.value)
    } else {
      setErrorCategorias((cats.reason as Error).message)
    }

    if (estados.status === 'fulfilled') {
      setPresupuestos(estados.value)
    } else {
      setErrorPresupuestos((estados.reason as Error).message)
    }

    setCargando(false)
  }, [filtro])

  // cargar cambia cada vez que cambia el filtro, asi que esto tambien recarga
  // la lista al elegir otra categoria.
  useEffect(() => {
    void cargar()
  }, [cargar])

  const saldo = calcularSaldo(transacciones)

  let visibles: Transaccion[] | null
  if (filtro === null) {
    visibles = transacciones
  } else {
    // Mientras llega la respuesta del filtro nuevo, no se muestra la del anterior.
    visibles = filtradas?.categoriaId === filtro ? filtradas.movimientos : null
  }

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

      {/* Arriba de todo, para que se vea sin bajar: es la alerta que pide la #13. */}
      <AlertasPresupuesto presupuestos={presupuestos} />

      {cargando && <p>Cargando movimientos…</p>}

      {errorMovimientos && (
        <p role="alert" className="error">
          No se pudieron cargar los movimientos: {errorMovimientos}
        </p>
      )}

      {!cargando && errorCategorias && (
        <p role="alert" className="error">
          No se pudieron cargar las categorias, asi que no se pueden agregar movimientos:{' '}
          {errorCategorias}
        </p>
      )}

      {!cargando && categorias.length > 0 && (
        <FormularioTransaccion categorias={categorias} onCreada={cargar} />
      )}

      {!cargando && !errorCategorias && <FormularioCategoria onCreada={cargar} />}

      {!cargando && errorPresupuestos && (
        <p role="alert" className="error">
          No se pudieron cargar los presupuestos: {errorPresupuestos}
        </p>
      )}

      {!cargando && !errorPresupuestos && categorias.length > 0 && (
        <PresupuestosDelMes presupuestos={presupuestos} categorias={categorias} onDefinido={cargar} />
      )}

      <div className="titulo-lista">
        <h3>Movimientos</h3>
        {categorias.length > 0 && (
          <FiltroCategoria categorias={categorias} valor={filtro} onCambiar={setFiltro} />
        )}
      </div>

      {!cargando && !errorMovimientos && visibles === null && <p>Cargando movimientos…</p>}

      {!cargando && !errorMovimientos && visibles !== null && (
        <ListaTransacciones
          transacciones={visibles}
          vacio={
            filtro === null
              ? 'Todavia no hay movimientos. Agrega el primero con el formulario de arriba.'
              : 'No hay movimientos en esta categoria.'
          }
        />
      )}
    </section>
  )
}
