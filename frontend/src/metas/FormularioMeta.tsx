import { useState, type FormEvent } from 'react'
import { crearMeta } from '../api/client'

type Props = {
  onCreada: () => void
}

/**
 * Ninguna etiqueta dice "Monto": la prueba E2E (#61) busca el campo Monto del
 * formulario de movimientos por coincidencia parcial, y encontraria dos.
 */
export function FormularioMeta({ onCreada }: Props) {
  const [nombre, setNombre] = useState('')
  const [objetivo, setObjetivo] = useState('')
  const [fechaLimite, setFechaLimite] = useState('')
  const [error, setError] = useState<string | null>(null)
  const [enviando, setEnviando] = useState(false)

  async function manejarEnvio(evento: FormEvent) {
    evento.preventDefault()
    setError(null)
    setEnviando(true)

    try {
      await crearMeta({ name: nombre.trim(), targetAmount: Number(objetivo), targetDate: fechaLimite })
      setNombre('')
      setObjetivo('')
      setFechaLimite('')
      onCreada()
    } catch (fallo) {
      setError((fallo as Error).message)
    } finally {
      setEnviando(false)
    }
  }

  return (
    <form onSubmit={manejarEnvio}>
      <h3>Nueva meta</h3>

      <label htmlFor="meta-nombre">Nombre de la meta</label>
      <input
        id="meta-nombre"
        type="text"
        maxLength={120}
        value={nombre}
        onChange={(e) => setNombre(e.target.value)}
        required
      />

      <label htmlFor="meta-objetivo">Cuanto queres juntar</label>
      <input
        id="meta-objetivo"
        type="number"
        inputMode="decimal"
        step="0.01"
        min="0.01"
        value={objetivo}
        onChange={(e) => setObjetivo(e.target.value)}
        required
      />

      <label htmlFor="meta-fecha">Fecha limite</label>
      <input
        id="meta-fecha"
        type="date"
        value={fechaLimite}
        onChange={(e) => setFechaLimite(e.target.value)}
        required
      />

      {error && (
        <p role="alert" className="error">
          {error}
        </p>
      )}

      <button type="submit" disabled={enviando}>
        {enviando ? 'Creando…' : 'Crear meta'}
      </button>
    </form>
  )
}
