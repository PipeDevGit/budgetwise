import { useState, type FormEvent } from 'react'
import { crearTransaccion, type Categoria, type TipoTransaccion } from '../api/client'

type Props = {
  categorias: Categoria[]
  onCreada: () => void
}

/**
 * La fecha de hoy en el formato AAAA-MM-DD que usan el input type="date" y la
 * API. Se arma con los metodos locales y no con toISOString(), que devuelve
 * UTC: en Costa Rica (UTC-6) despues de las 6 de la tarde eso daria el dia
 * siguiente.
 */
function hoy(): string {
  const ahora = new Date()
  const mes = String(ahora.getMonth() + 1).padStart(2, '0')
  const dia = String(ahora.getDate()).padStart(2, '0')
  return `${ahora.getFullYear()}-${mes}-${dia}`
}

export function FormularioTransaccion({ categorias, onCreada }: Props) {
  const [monto, setMonto] = useState('')
  const [tipo, setTipo] = useState<TipoTransaccion>('GASTO')
  const [fecha, setFecha] = useState(hoy)
  const [categoriaId, setCategoriaId] = useState('')
  const [descripcion, setDescripcion] = useState('')
  const [error, setError] = useState<string | null>(null)
  const [enviando, setEnviando] = useState(false)

  async function manejarEnvio(evento: FormEvent) {
    evento.preventDefault()
    setError(null)
    setEnviando(true)

    try {
      await crearTransaccion({
        amount: Number(monto),
        type: tipo,
        date: fecha,
        categoryId: Number(categoriaId),
        // La descripcion es opcional: si esta vacia se manda null y no "".
        description: descripcion.trim() === '' ? null : descripcion.trim(),
      })
      setMonto('')
      setDescripcion('')
      onCreada()
    } catch (fallo) {
      setError((fallo as Error).message)
    } finally {
      setEnviando(false)
    }
  }

  return (
    <form onSubmit={manejarEnvio}>
      <h3>Agregar movimiento</h3>

      <label htmlFor="tipo">Tipo</label>
      <select
        id="tipo"
        value={tipo}
        onChange={(e) => setTipo(e.target.value as TipoTransaccion)}
        required
      >
        <option value="GASTO">Gasto</option>
        <option value="INGRESO">Ingreso</option>
      </select>

      <label htmlFor="monto">Monto</label>
      <input
        id="monto"
        type="number"
        inputMode="decimal"
        step="0.01"
        min="0.01"
        value={monto}
        onChange={(e) => setMonto(e.target.value)}
        required
      />

      <label htmlFor="fecha">Fecha</label>
      <input
        id="fecha"
        type="date"
        value={fecha}
        onChange={(e) => setFecha(e.target.value)}
        required
      />

      <label htmlFor="categoria">Categoria</label>
      <select
        id="categoria"
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

      <label htmlFor="descripcion">Descripcion (opcional)</label>
      <input
        id="descripcion"
        type="text"
        maxLength={255}
        value={descripcion}
        onChange={(e) => setDescripcion(e.target.value)}
      />

      {error && (
        <p role="alert" className="error">
          {error}
        </p>
      )}

      <button type="submit" disabled={enviando}>
        {enviando ? 'Agregando…' : 'Agregar'}
      </button>
    </form>
  )
}
