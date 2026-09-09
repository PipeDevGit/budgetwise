import { useState, type FormEvent } from 'react'
import { iniciarSesion } from '../api/client'

type Props = {
  onSesionIniciada: (token: string) => void
  onIrARegistro: () => void
}

export function LoginForm({ onSesionIniciada, onIrARegistro }: Props) {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState<string | null>(null)
  const [enviando, setEnviando] = useState(false)

  async function manejarEnvio(evento: FormEvent) {
    // Sin esto el navegador recarga la pagina y se pierde el estado.
    evento.preventDefault()
    setError(null)
    setEnviando(true)

    try {
      const { token } = await iniciarSesion({ email, password })
      onSesionIniciada(token)
    } catch (fallo) {
      setError((fallo as Error).message)
    } finally {
      setEnviando(false)
    }
  }

  return (
    <form onSubmit={manejarEnvio}>
      <h2>Iniciar sesion</h2>

      <label htmlFor="login-email">Correo</label>
      <input
        id="login-email"
        type="email"
        value={email}
        onChange={(e) => setEmail(e.target.value)}
        required
        autoComplete="email"
      />

      <label htmlFor="login-password">Contrasena</label>
      <input
        id="login-password"
        type="password"
        value={password}
        onChange={(e) => setPassword(e.target.value)}
        required
        autoComplete="current-password"
      />

      {error && <p role="alert" className="error">{error}</p>}

      <button type="submit" disabled={enviando}>
        {enviando ? 'Entrando…' : 'Entrar'}
      </button>

      <p>
        No tenes cuenta?{' '}
        <button type="button" className="enlace" onClick={onIrARegistro}>
          Registrate
        </button>
      </p>
    </form>
  )
}
