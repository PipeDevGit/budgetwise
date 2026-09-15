import { obtenerRecomendaciones, type RespuestaRecomendaciones } from '../api/client'

/** Lo que la seccion puede mostrar despues de pedir las recomendaciones. */
export type ResultadoRecomendaciones =
  | { estado: 'listo'; datos: RespuestaRecomendaciones }
  | { estado: 'error'; mensaje: string }

/**
 * Pide las recomendaciones y convierte la respuesta en algo que la pantalla solo
 * tiene que dibujar. Vive fuera del componente para poder probar el caso de
 * exito y el de error sin montar React; las pruebas reemplazan `pedir`.
 */
export async function cargarRecomendaciones(
  pedir: () => Promise<RespuestaRecomendaciones> = obtenerRecomendaciones,
): Promise<ResultadoRecomendaciones> {
  try {
    const datos = await pedir()
    // Una seccion de recomendaciones sin ningun consejo no le sirve al usuario.
    if (datos.recommendations.length === 0) {
      return { estado: 'error', mensaje: 'La API no devolvio recomendaciones' }
    }
    return { estado: 'listo', datos }
  } catch (fallo) {
    return { estado: 'error', mensaje: (fallo as Error).message }
  }
}
