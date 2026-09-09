import { useEffect, useState } from 'react'
import { getHealth } from './api/client'

type Estado =
  | { tipo: 'cargando' }
  | { tipo: 'conectado'; aplicacion: string }
  | { tipo: 'error'; mensaje: string }

/**
 * Pantalla temporal del andamiaje (issue #1): comprueba que el frontend
 * alcanza al backend. La reemplazan el login (#5) y el dashboard (#11).
 */
function App() {
  const [estado, setEstado] = useState<Estado>({ tipo: 'cargando' })

  useEffect(() => {
    getHealth()
      .then((data) => setEstado({ tipo: 'conectado', aplicacion: data.application }))
      .catch((error: Error) => setEstado({ tipo: 'error', mensaje: error.message }))
  }, [])

  return (
    <main>
      <h1>BudgetWise</h1>
      <p>MVP de gestion de presupuesto personal</p>

      {estado.tipo === 'cargando' && <p>Conectando con la API…</p>}

      {estado.tipo === 'conectado' && (
        <p>Conectado a la API: <strong>{estado.aplicacion}</strong></p>
      )}

      {estado.tipo === 'error' && (
        <p>
          No se pudo conectar con la API: {estado.mensaje}
          <br />
          Verifica que el backend este corriendo en el puerto 8080.
        </p>
      )}
    </main>
  )
}

export default App
