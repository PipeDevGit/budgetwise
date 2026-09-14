import type { Categoria } from '../api/client'
import { categoriaElegida } from './filtro'

type Props = {
  categorias: Categoria[]
  valor: number | null
  onCambiar: (categoriaId: number | null) => void
}

export function FiltroCategoria({ categorias, valor, onCambiar }: Props) {
  return (
    <div className="filtro">
      <label htmlFor="filtro-categoria">Ver</label>
      <select
        id="filtro-categoria"
        value={valor ?? ''}
        onChange={(e) => onCambiar(categoriaElegida(e.target.value))}
      >
        <option value="">Todas las categorias</option>
        {categorias.map((categoria) => (
          <option key={categoria.id} value={categoria.id}>
            {categoria.name}
          </option>
        ))}
      </select>
    </div>
  )
}
