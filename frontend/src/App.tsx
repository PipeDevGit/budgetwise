import { useState } from 'react'
import { LoginForm } from './auth/LoginForm'
import { RegisterForm } from './auth/RegisterForm'
import { borrarToken, guardarToken, leerToken } from './auth/session'
import { PanelControl } from './panel/PanelControl'
import { PantallaTransacciones } from './transacciones/PantallaTransacciones'

type Vista = 'login' | 'registro'

/**
 * Secciones de la sesion abierta. Arranca en transacciones: es donde se cargan
 * los datos que el panel resume, y es lo que espera la prueba E2E de la #18.
 */
type Seccion = 'transacciones' | 'panel'

function App() {
  // Leer el token al arrancar es lo que hace que la sesion sobreviva a
  // recargar la pagina: si ya hay uno guardado, se entra directo al panel.
  const [token, setToken] = useState<string | null>(() => leerToken())
  const [vista, setVista] = useState<Vista>('login')
  const [seccion, setSeccion] = useState<Seccion>('transacciones')

  function abrirSesion(nuevoToken: string) {
    guardarToken(nuevoToken)
    setToken(nuevoToken)
  }

  function cerrarSesion() {
    borrarToken()
    setToken(null)
    setVista('login')
    setSeccion('transacciones')
  }

  let contenido
  if (token) {
    contenido = (
      <>
        <nav className="secciones" aria-label="Secciones">
          <button
            type="button"
            aria-pressed={seccion === 'transacciones'}
            onClick={() => setSeccion('transacciones')}
          >
            Transacciones
          </button>
          <button type="button" aria-pressed={seccion === 'panel'} onClick={() => setSeccion('panel')}>
            Panel
          </button>
        </nav>
        {seccion === 'panel' ? (
          <PanelControl onCerrarSesion={cerrarSesion} />
        ) : (
          <PantallaTransacciones onCerrarSesion={cerrarSesion} />
        )}
      </>
    )
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
