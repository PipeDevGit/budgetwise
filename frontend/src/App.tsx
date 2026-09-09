import { useState } from 'react'
import { LoginForm } from './auth/LoginForm'
import { RegisterForm } from './auth/RegisterForm'
import { borrarToken, guardarToken, leerToken } from './auth/session'

type Vista = 'login' | 'registro'

/**
 * Marcador de posicion. El panel de control de verdad es la issue #11; aca
 * solo hace falta un destino al que llegar despues de iniciar sesion, que es
 * lo que pide el criterio de la #5.
 */
function Dashboard({ onCerrarSesion }: { onCerrarSesion: () => void }) {
  return (
    <section>
      <h2>Panel de control</h2>
      <p>Sesion iniciada. El panel real llega con la issue #11.</p>
      <button type="button" onClick={onCerrarSesion}>
        Cerrar sesion
      </button>
    </section>
  )
}

function App() {
  // Leer el token al arrancar es lo que hace que la sesion sobreviva a
  // recargar la pagina: si ya hay uno guardado, se entra directo al panel.
  const [token, setToken] = useState<string | null>(() => leerToken())
  const [vista, setVista] = useState<Vista>('login')

  function abrirSesion(nuevoToken: string) {
    guardarToken(nuevoToken)
    setToken(nuevoToken)
  }

  function cerrarSesion() {
    borrarToken()
    setToken(null)
    setVista('login')
  }

  let contenido
  if (token) {
    contenido = <Dashboard onCerrarSesion={cerrarSesion} />
  } else if (vista === 'login') {
    contenido = (
      <LoginForm onSesionIniciada={abrirSesion} onIrARegistro={() => setVista('registro')} />
    )
  } else {
    contenido = (
      <RegisterForm onSesionIniciada={abrirSesion} onIrALogin={() => setVista('login')} />
    )
  }

  return (
    <main>
      <h1>BudgetWise</h1>
      {contenido}
    </main>
  )
}

export default App
