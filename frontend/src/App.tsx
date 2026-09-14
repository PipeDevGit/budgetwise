import { useState } from 'react'
import { LoginForm } from './auth/LoginForm'
import { RegisterForm } from './auth/RegisterForm'
import { borrarToken, guardarToken, leerToken } from './auth/session'
import { PantallaTransacciones } from './transacciones/PantallaTransacciones'

type Vista = 'login' | 'registro'

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
    contenido = <PantallaTransacciones onCerrarSesion={cerrarSesion} />
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
