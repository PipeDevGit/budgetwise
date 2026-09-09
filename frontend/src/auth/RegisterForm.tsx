import { useState, type FormEvent } from 'react'
import { registrar } from '../api/client'

type Props = {
  onSesionIniciada: (token: string) => void
  onIrALogin: () => void
}

export function RegisterForm({ onSesionIniciada, onIrALogin }: Props) {
  const [name, setName] = useState('')
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState<string | null>(null)
  const [enviando, setEnviando] = useState(false)

  async function manejarEnvio(evento: FormEvent) {
    evento.preventDefault()
    setError(null)
    setEnviando(true)

    try {
      // El backend devuelve el token al registrar, asi que el usuario entra
      // directo sin tener que iniciar sesion otra vez.
      const { token } = await registrar({ name, email, password })
      onSesionIniciada(token)
    } catch (fallo) {
      setError((fallo as Error).message)
    } finally {
      setEnviando(false)
    }
  }

  return (
    <form onSubmit={manejarEnvio}>
      <h2>Crear cuenta</h2>

      <label htmlFor="registro-name">Nombre</label>
      <input
        id="registro-name"
        type="text"
        value={name}
        onChange={(e) => setName(e.target.value)}
        required
        maxLength={120}
        autoComplete="name"
      />

      <label htmlFor="registro-email">Correo</label>
      <input
        id="registro-email"
        type="email"
        value={email}
        onChange={(e) => setEmail(e.target.value)}
        required
        maxLength={180}
        autoComplete="email"
      />

      <label htmlFor="registro-password">Contrasena</label>
      <input
        id="registro-password"
        type="password"
        value={password}
        onChange={(e) => setPassword(e.target.value)}
        required
        minLength={8}
        autoComplete="new-password"
      />
      <small>Minimo 8 caracteres.</small>

      {error && <p role="alert" className="error">{error}</p>}

      <button type="submit" disabled={enviando}>
        {enviando ? 'Creando…' : 'Crear cuenta'}
      </button>

      <p>
        Ya tenes cuenta?{' '}
        <button type="button" className="enlace" onClick={onIrALogin}>
          Inicia sesion
        </button>
      </p>
    </form>
  )
}
