import { useEffect, useRef, useState } from 'react'
import { cargarRecomendaciones, type ResultadoRecomendaciones } from './cargarRecomendaciones'
import { describirFuente } from './fuenteRecomendaciones'

/**
 * Las tres recomendaciones del mes (issue #17). Se cargan aparte del resto del
 * panel: la llamada a Gemini puede tardar hasta 10 segundos (D-12), y el saldo
 * y el grafico no tienen por que esperarla.
 *
 * La decision entre exito y error vive en cargarRecomendaciones, que tiene sus
 * pruebas; este componente solo la dibuja.
 */
export function Recomendaciones() {
  const [resultado, setResultado] = useState<ResultadoRecomendaciones | null>(null)
  const pedida = useRef(false)

  useEffect(() => {
    // En desarrollo StrictMode ejecuta los efectos dos veces. Sin esta guarda,
    // cada vez que se abre el panel se gastarian dos llamadas de la cuota
    // gratuita de Gemini.
    if (pedida.current) {
      return
    }
    pedida.current = true

    void cargarRecomendaciones().then(setResultado)
  }, [])

  return (
    <div>
      <h3>Recomendaciones del mes</h3>

      {!resultado && <p>Preparando tus recomendaciones…</p>}

      {resultado?.estado === 'error' && (
        <p role="alert" className="error">
          No se pudieron cargar las recomendaciones: {resultado.mensaje}
        </p>
      )}

      {resultado?.estado === 'listo' && (
        <>
          <ol>
            {resultado.datos.recommendations.map((consejo, indice) => (
              <li key={indice}>{consejo}</li>
            ))}
          </ol>
          <p className="etiqueta">{describirFuente(resultado.datos.source)}</p>
        </>
      )}
    </div>
  )
}
