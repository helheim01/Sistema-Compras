import { useEffect, useState } from 'react'
import {
  listarCategorias, agregarCategoria, modificarCategoria,
  eliminarCategoria, activarCategoria, desactivarCategoria
} from '../api/categoriaApi'

export default function Categorias() {
  const [categorias, setCategorias] = useState([])
  const [form, setForm] = useState({ nombre: '', activa: true })
  const [editando, setEditando] = useState(null)
  const [error, setError] = useState('')

  useEffect(() => { cargar() }, [])

  const cargar = async () => {
    try {
      const res = await listarCategorias()
      setCategorias(res.data)
    } catch {
      setError('Error al cargar categorías')
    }
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    try {
      if (editando) {
        await modificarCategoria({ ...form, id: editando })
      } else {
        await agregarCategoria(form)
      }
      setForm({ nombre: '', activa: true })
      setEditando(null)
      cargar()
    } catch (err) {
      setError(err.response?.data?.error || 'Error al guardar')
    }
  }

  const handleEditar = (cat) => {
    setEditando(cat.id)
    setForm({ nombre: cat.nombre, activa: cat.activa })
  }

  const handleEliminar = async (id) => {
    if (!confirm('¿Eliminar esta categoría?')) return
    try {
      await eliminarCategoria(id)
      cargar()
    } catch (err) {
      setError(err.response?.data?.error || 'Error al eliminar')
    }
  }

  const handleToggle = async (cat) => {
    try {
      if (cat.activa) {
        await desactivarCategoria(cat.id)
      } else {
        await activarCategoria(cat.id)
      }
      cargar()
    } catch {
      setError('Error al cambiar estado')
    }
  }

  return (
    <div className="container py-4">
      <h2 className="mb-4">Categorías</h2>

      {error && (
        <div className="alert alert-danger alert-dismissible">
          {error}
          <button className="btn-close" onClick={() => setError('')} />
        </div>
      )}

      {/* Formulario */}
      <div className="card mb-4">
        <div className="card-header">
          {editando ? 'Editar categoría' : 'Nueva categoría'}
        </div>
        <div className="card-body">
          <form onSubmit={handleSubmit} className="row g-3">
            <div className="col-md-6">
              <label className="form-label">Nombre</label>
              <input
                type="text"
                className="form-control"
                value={form.nombre}
                onChange={(e) => setForm({ ...form, nombre: e.target.value })}
                required
              />
            </div>
            <div className="col-md-3 d-flex align-items-end">
              <div className="form-check">
                <input
                  type="checkbox"
                  className="form-check-input"
                  id="activa"
                  checked={form.activa}
                  onChange={(e) => setForm({ ...form, activa: e.target.checked })}
                />
                <label className="form-check-label" htmlFor="activa">Activa</label>
              </div>
            </div>
            <div className="col-md-3 d-flex align-items-end gap-2">
              <button type="submit" className="btn btn-primary">
                {editando ? 'Guardar cambios' : 'Agregar'}
              </button>
              {editando && (
                <button type="button" className="btn btn-secondary"
                  onClick={() => { setEditando(null); setForm({ nombre: '', activa: true }) }}>
                  Cancelar
                </button>
              )}
            </div>
          </form>
        </div>
      </div>

      {/* Tabla */}
      <div className="card">
        <div className="card-body">
          <table className="table table-hover align-middle">
            <thead className="table-dark">
              <tr>
                <th>ID</th>
                <th>Nombre</th>
                <th>Estado</th>
                <th>Acciones</th>
              </tr>
            </thead>
            <tbody>
              {categorias.length === 0 ? (
                <tr><td colSpan="4" className="text-center text-muted">Sin categorías</td></tr>
              ) : (
                categorias.map(cat => (
                  <tr key={cat.id}>
                    <td>{cat.id}</td>
                    <td>{cat.nombre}</td>
                    <td>
                      <span className={`badge ${cat.activa ? 'bg-success' : 'bg-secondary'}`}>
                        {cat.activa ? 'Activa' : 'Inactiva'}
                      </span>
                    </td>
                    <td className="d-flex gap-2">
                      <button className="btn btn-sm btn-warning" onClick={() => handleEditar(cat)}>
                        Editar
                      </button>
                      <button
                        className={`btn btn-sm ${cat.activa ? 'btn-outline-secondary' : 'btn-outline-success'}`}
                        onClick={() => handleToggle(cat)}>
                        {cat.activa ? 'Desactivar' : 'Activar'}
                      </button>
                      <button className="btn btn-sm btn-danger" onClick={() => handleEliminar(cat.id)}>
                        Eliminar
                      </button>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  )
}