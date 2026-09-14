import { useEffect, useRef, useState } from 'react'
import { obtenerRecomendaciones, type RespuestaRecomendaciones } from '../api/client'
import { describirFuente } from './fuenteRecomendaciones'

/**
 * Las tres recomendaciones del mes (issue #17). Se cargan aparte del resto del
 * panel: la llamada a Gemini puede tardar hasta 10 segundos (D-12), y el saldo
 * y el grafico no tienen por que esperarla.
 */
export function Recomendaciones() {
  const [datos, setDatos] = useState<RespuestaRecomendaciones | null>(null)
  const [error, setError] = useState<string | null>(null)
  const pedida = useRef(false)

  useEffect(() => {
    // En desarrollo StrictMode ejecuta los efectos dos veces. Sin esta guarda,
    // cada vez que se abre el panel se gastarian dos llamadas de la cuota
    // gratuita de Gemini.
    if (pedida.current) {
      return
    }
    pedida.current = true

    obtenerRecomendaciones()
      .then(setDatos)
      .catch((fallo: Error) => setError(fallo.message))
  }, [])

  return (
    <div>
      <h3>Recomendaciones del mes</h3>

      {!datos && !error && <p>Preparando tus recomendaciones…</p>}

      {error && (
        <p role="alert" className="error">
          No se pudieron cargar las recomendaciones: {error}
        </p>
      )}

      {datos && (
        <>
          <ol>
            {datos.recommendations.map((consejo, indice) => (
              <li key={indice}>{consejo}</li>
            ))}
          </ol>
          <p className="etiqueta">{describirFuente(datos.source)}</p>
        </>
      )}
    </div>
  )
}
