import { useCallback, useEffect, useState } from 'react'
import { listarMetas, type Meta } from '../api/client'
import { FormularioMeta } from './FormularioMeta'
import { TarjetaMeta } from './TarjetaMeta'

type Props = {
  onCerrarSesion: () => void
}

export function MetasDeAhorro({ onCerrarSesion }: Props) {
  const [metas, setMetas] = useState<Meta[]>([])
  const [cargando, setCargando] = useState(true)
  const [error, setError] = useState<string | null>(null)

  const cargar = useCallback(async () => {
    try {
      const lista = await listarMetas()
      setMetas(lista)
      setError(null)
    } catch (fallo) {
      setError((fallo as Error).message)
    } finally {
      setCargando(false)
    }
  }, [])

  useEffect(() => {
    void cargar()
  }, [cargar])

  return (
    <section className="metas">
      <header className="cabecera">
        <h2>Metas de ahorro</h2>
        <button type="button" onClick={onCerrarSesion}>
          Cerrar sesion
        </button>
      </header>

      {cargando && <p>Cargando metas…</p>}

      {error && (
        <p role="alert" className="error">
          No se pudieron cargar las metas: {error}
        </p>
      )}

      {!cargando && !error && metas.length === 0 && <p>Todavia no tenes metas. Crea la primera abajo.</p>}

      {metas.map((meta) => (
        <TarjetaMeta key={meta.id} meta={meta} onActualizada={cargar} />
      ))}

      {!cargando && <FormularioMeta onCreada={cargar} />}
    </section>
  )
}
