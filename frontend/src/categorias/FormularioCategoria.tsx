import { useState, type FormEvent } from 'react'
import { crearCategoria } from '../api/client'

type Props = {
  onCreada: () => void
}

export function FormularioCategoria({ onCreada }: Props) {
  const [nombre, setNombre] = useState('')
  const [error, setError] = useState<string | null>(null)
  const [enviando, setEnviando] = useState(false)

  async function manejarEnvio(evento: FormEvent) {
    evento.preventDefault()
    setError(null)
    setEnviando(true)

    try {
      await crearCategoria(nombre.trim())
      setNombre('')
      onCreada()
    } catch (fallo) {
      // Si el nombre ya existe, la API responde 409 con su propio mensaje
      // ("Ya existe una categoria con ese nombre") y es ese el que se muestra.
      setError((fallo as Error).message)
    } finally {
      setEnviando(false)
    }
  }

  return (
    <form onSubmit={manejarEnvio}>
      <h3>Nueva categoria</h3>

      <label htmlFor="nombre-categoria">Nombre</label>
      <input
        id="nombre-categoria"
        type="text"
        maxLength={80}
        value={nombre}
        onChange={(e) => setNombre(e.target.value)}
        required
      />

      {error && (
        <p role="alert" className="error">
          {error}
        </p>
      )}

      <button type="submit" disabled={enviando}>
        {enviando ? 'Creando…' : 'Crear categoria'}
      </button>
    </form>
  )
}
