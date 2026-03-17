import { useEffect, useState } from 'react'
import {
  listarProductos, agregarProducto, modificarProducto,
  eliminarProducto, activarProducto, desactivarProducto
} from '../api/productosApi'
import { listarCategorias } from '../api/categoriaApi'

const formInicial = {
  nombre: '', codigoProducto: '', descripcion: '', precio: '',
  stock: '', proveedor: '', imagenUrl: '', activo: true,
  categoria: { id: '' }
}

export default function Productos() {
  const [productos, setProductos] = useState([])
  const [categorias, setCategorias] = useState([])
  const [form, setForm] = useState(formInicial)
  const [editando, setEditando] = useState(null)
  const [error, setError] = useState('')

  useEffect(() => {
    cargar()
    cargarCategorias()
  }, [])

  const cargar = async () => {
    try {
      const res = await listarProductos()
      setProductos(res.data)
    } catch {
      setError('Error al cargar productos')
    }
  }

  const cargarCategorias = async () => {
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
      const payload = {
        ...form,
        precio: parseFloat(form.precio),
        stock: parseInt(form.stock),
        categoria: { id: parseInt(form.categoria.id) }
      }
      if (editando) {
        await modificarProducto({ ...payload, id: editando })
      } else {
        await agregarProducto(payload)
      }
      setForm(formInicial)
      setEditando(null)
      cargar()
    } catch (err) {
      setError(err.response?.data?.error || 'Error al guardar')
    }
  }

  const handleEditar = (p) => {
    setEditando(p.id)
    setForm({
      nombre: p.nombre,
      codigoProducto: p.codigoProducto,
      descripcion: p.descripcion || '',
      precio: p.precio,
      stock: p.stock,
      proveedor: p.proveedor || '',
      imagenUrl: p.imagenUrl || '',
      activo: p.activo,
      categoria: { id: p.categoria?.id || '' }
    })
  }

  const handleEliminar = async (id) => {
    if (!confirm('¿Eliminar este producto?')) return
    try {
      await eliminarProducto(id)
      cargar()
    } catch (err) {
      setError(err.response?.data?.error || 'Error al eliminar')
    }
  }

  const handleToggle = async (p) => {
    try {
      if (p.activo) {
        await desactivarProducto(p.id)
      } else {
        await activarProducto(p.id)
      }
      cargar()
    } catch {
      setError('Error al cambiar estado')
    }
  }

  return (
    <div className="container py-4">
      <h2 className="mb-4">Productos</h2>

      {error && (
        <div className="alert alert-danger alert-dismissible">
          {error}
          <button className="btn-close" onClick={() => setError('')} />
        </div>
      )}

      {/* Formulario */}
      <div className="card mb-4">
        <div className="card-header">
          {editando ? 'Editar producto' : 'Nuevo producto'}
        </div>
        <div className="card-body">
          <form onSubmit={handleSubmit} className="row g-3">
            <div className="col-md-4">
              <label className="form-label">Nombre</label>
              <input type="text" className="form-control"
                value={form.nombre}
                onChange={(e) => setForm({ ...form, nombre: e.target.value })}
                required />
            </div>
            <div className="col-md-4">
              <label className="form-label">Código</label>
              <input type="text" className="form-control"
                value={form.codigoProducto}
                onChange={(e) => setForm({ ...form, codigoProducto: e.target.value })}
                required />
            </div>
            <div className="col-md-4">
              <label className="form-label">Categoría</label>
              <select className="form-select"
                value={form.categoria.id}
                onChange={(e) => setForm({ ...form, categoria: { id: e.target.value } })}
                required>
                <option value="">Seleccionar...</option>
                {categorias.map(c => (
                  <option key={c.id} value={c.id}>{c.nombre}</option>
                ))}
              </select>
            </div>
            <div className="col-md-8">
              <label className="form-label">Descripción</label>
              <input type="text" className="form-control"
                value={form.descripcion}
                onChange={(e) => setForm({ ...form, descripcion: e.target.value })} />
            </div>
            <div className="col-md-4">
              <label className="form-label">Proveedor</label>
              <input type="text" className="form-control"
                value={form.proveedor}
                onChange={(e) => setForm({ ...form, proveedor: e.target.value })} />
            </div>
            <div className="col-md-3">
              <label className="form-label">Precio</label>
              <input type="number" step="0.01" className="form-control"
                value={form.precio}
                onChange={(e) => setForm({ ...form, precio: e.target.value })}
                required />
            </div>
            <div className="col-md-3">
              <label className="form-label">Stock</label>
              <input type="number" className="form-control"
                value={form.stock}
                onChange={(e) => setForm({ ...form, stock: e.target.value })}
                required />
            </div>
            <div className="col-md-4">
              <label className="form-label">URL Imagen</label>
              <input type="text" className="form-control"
                value={form.imagenUrl}
                onChange={(e) => setForm({ ...form, imagenUrl: e.target.value })} />
            </div>
            <div className="col-md-2 d-flex align-items-end">
              <div className="form-check">
                <input type="checkbox" className="form-check-input" id="activo"
                  checked={form.activo}
                  onChange={(e) => setForm({ ...form, activo: e.target.checked })} />
                <label className="form-check-label" htmlFor="activo">Activo</label>
              </div>
            </div>
            <div className="col-12 d-flex gap-2">
              <button type="submit" className="btn btn-primary">
                {editando ? 'Guardar cambios' : 'Agregar'}
              </button>
              {editando && (
                <button type="button" className="btn btn-secondary"
                  onClick={() => { setEditando(null); setForm(formInicial) }}>
                  Cancelar
                </button>
              )}
            </div>
          </form>
        </div>
      </div>

      {/* Tabla */}
      <div className="card">
        <div className="card-body table-responsive">
          <table className="table table-hover align-middle">
            <thead className="table-dark">
              <tr>
                <th>ID</th>
                <th>Nombre</th>
                <th>Código</th>
                <th>Categoría</th>
                <th>Precio</th>
                <th>Stock</th>
                <th>Estado</th>
                <th>Acciones</th>
              </tr>
            </thead>
            <tbody>
              {productos.length === 0 ? (
                <tr><td colSpan="8" className="text-center text-muted">Sin productos</td></tr>
              ) : (
                productos.map(p => (
                  <tr key={p.id}>
                    <td>{p.id}</td>
                    <td>{p.nombre}</td>
                    <td><code>{p.codigoProducto}</code></td>
                    <td>{p.categoria?.nombre || '-'}</td>
                    <td>${p.precio?.toFixed(2)}</td>
                    <td>
                      <span className={`badge ${p.stock > 0 ? 'bg-info' : 'bg-danger'}`}>
                        {p.stock}
                      </span>
                    </td>
                    <td>
                      <span className={`badge ${p.activo ? 'bg-success' : 'bg-secondary'}`}>
                        {p.activo ? 'Activo' : 'Inactivo'}
                      </span>
                    </td>
                    <td className="d-flex gap-2">
                      <button className="btn btn-sm btn-warning"
                        onClick={() => handleEditar(p)}>
                        Editar
                      </button>
                      <button
                        className={`btn btn-sm ${p.activo ? 'btn-outline-secondary' : 'btn-outline-success'}`}
                        onClick={() => handleToggle(p)}>
                        {p.activo ? 'Desactivar' : 'Activar'}
                      </button>
                      <button className="btn btn-sm btn-danger"
                        onClick={() => handleEliminar(p.id)}>
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