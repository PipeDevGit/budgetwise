import { useState, type FormEvent } from 'react'
import { actualizarAhorro, type Meta } from '../api/client'
import { formatearMonto } from '../transacciones/montos'
import { metaCumplida } from './progreso'

type Props = {
  meta: Meta
  onActualizada: () => void
}

export function TarjetaMeta({ meta, onActualizada }: Props) {
  const [ahorrado, setAhorrado] = useState(String(meta.savedAmount))
  const [error, setError] = useState<string | null>(null)
  const [enviando, setEnviando] = useState(false)

  async function manejarEnvio(evento: FormEvent) {
    evento.preventDefault()
    setError(null)
    setEnviando(true)

    try {
      await actualizarAhorro(meta.id, Number(ahorrado))
      onActualizada()
    } catch (fallo) {
      setError((fallo as Error).message)
    } finally {
      setEnviando(false)
    }
  }

  return (
    <article className="tarjeta-meta">
      <div className="cabecera-meta">
        <strong>{meta.name}</strong>
        <span>{metaCumplida(meta) ? 'Meta cumplida' : `${meta.progressPercent} %`}</span>
      </div>

      {/* La barra es el <progress> del navegador: el porcentaje ya viene calculado por la API. */}
      <progress value={meta.progressPercent} max={100} aria-label={`Progreso de ${meta.name}`} />

      <p className="detalle-meta">
        {formatearMonto(meta.savedAmount)} de {formatearMonto(meta.targetAmount)} · hasta el{' '}
        {meta.targetDate}
      </p>

      <form onSubmit={manejarEnvio}>
        <label htmlFor={`meta-ahorrado-${meta.id}`}>Llevo ahorrado en total</label>
        <input
          id={`meta-ahorrado-${meta.id}`}
          type="number"
          inputMode="decimal"
          step="0.01"
          min="0"
          value={ahorrado}
          onChange={(e) => setAhorrado(e.target.value)}
          required
        />

        {error && (
          <p role="alert" className="error">
            {error}
          </p>
        )}

        <button type="submit" disabled={enviando}>
          {enviando ? 'Guardando…' : 'Actualizar ahorro'}
        </button>
      </form>
    </article>
  )
}
