import { describe, expect, it } from 'vitest'
import { categoriaElegida } from './filtro'

describe('categoriaElegida', () => {
  // Es el caso que importa: Number('') daria 0 y la API filtraria por una
  // categoria que no existe.
  it('la opcion vacia es "todas las categorias", no la categoria 0', () => {
    expect(categoriaElegida('')).toBeNull()
  })

  it('cualquier otro valor es el id de la categoria', () => {
    expect(categoriaElegida('3')).toBe(3)
  })
})
