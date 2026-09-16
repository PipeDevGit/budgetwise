/**
 * El <select> del filtro solo maneja texto: "" significa "todas las
 * categorias" y cualquier otro valor es el id. Ojo que Number("") da 0 y no
 * null: sin esta conversion, volver a "todas" pediria ?categoryId=0 y la lista
 * saldria vacia.
 */
export function categoriaElegida(valor: string): number | null {
  return valor === '' ? null : Number(valor)
}
