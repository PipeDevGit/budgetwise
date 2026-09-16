import { useState, type FormEvent } from 'react'
import { definirPresupuesto, type Categoria } from '../api/client'

type Props = {
  categorias: Categoria[]
  onDefinido: () => void
}

/**
 * Las etiquetas no dicen "Categoria" ni "Monto" a proposito: la prueba E2E
 * (#61) busca esos campos del formulario de movimientos por coincidencia
 * parcial, y encontraria dos.
 */
export function FormularioPresupuesto({ categorias, onDefinido }: Props) {
  const [categoriaId, setCategoriaId] = useState('')
  const [limite, setLimite] = useState('')
  const [error, setError] = useState<string | null>(null)
  const [enviando, setEnviando] = useState(false)

  async function manejarEnvio(evento: FormEvent) {
    evento.preventDefault()
    setError(null)
    setEnviando(true)

    try {
      await definirPresupuesto(Number(categoriaId), Number(limite))
      setLimite('')
      onDefinido()
    } catch (fallo) {
      setError((fallo as Error).message)
    } finally {
      setEnviando(false)
    }
  }

  return (
    <form onSubmit={manejarEnvio}>
      <label htmlFor="presupuesto-categoria">Presupuesto para</label>
      <select
        id="presupuesto-categoria"
        value={categoriaId}
        onChange={(e) => setCategoriaId(e.target.value)}
        required
      >
        <option value="" disabled>
          Elegi una categoria
        </option>
        {categorias.map((categoria) => (
          <option key={categoria.id} value={categoria.id}>
            {categoria.name}
          </option>
        ))}
      </select>

      <label htmlFor="presupuesto-limite">Limite mensual</label>
      <input
        id="presupuesto-limite"
        type="number"
        inputMode="decimal"
        step="0.01"
        min="0.01"
        value={limite}
        onChange={(e) => setLimite(e.target.value)}
        required
      />
      <small>Si la categoria ya tiene presupuesto este mes, se reemplaza el limite.</small>

      {error && (
        <p role="alert" className="error">
          {error}
        </p>
      )}

      <button type="submit" disabled={enviando}>
        {enviando ? 'Guardando…' : 'Guardar presupuesto'}
      </button>
    </form>
  )
}
