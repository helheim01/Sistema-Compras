import { useEffect, useState } from 'react'
import {
  listarClientes, agregarCliente, modificarCliente, eliminarCliente
} from '../api/clienteApi'

const formInicial = {
  nombre: '', email: '', telefono: '', direccion: '',
  usuarioWeb: { id: '' }
}

export default function Clientes() {
  const [clientes, setClientes] = useState([])
  const [form, setForm] = useState(formInicial)
  const [editando, setEditando] = useState(null)
  const [error, setError] = useState('')

  useEffect(() => { cargar() }, [])

  const cargar = async () => {
    try {
      const res = await listarClientes()
      setClientes(res.data)
    } catch {
      setError('Error al cargar clientes')
    }
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    try {
      const payload = {
        ...form,
        usuarioWeb: { id: parseInt(form.usuarioWeb.id) }
      }
      if (editando) {
        await modificarCliente({ ...payload, id: editando })
      } else {
        await agregarCliente(payload)
      }
      setForm(formInicial)
      setEditando(null)
      cargar()
    } catch (err) {
      setError(err.response?.data?.error || 'Error al guardar')
    }
  }

  const handleEditar = (c) => {
    setEditando(c.id)
    setForm({
      nombre: c.nombre,
      email: c.email,
      telefono: c.telefono,
      direccion: c.direccion,
      usuarioWeb: { id: c.usuarioWeb?.id || '' }
    })
  }

  const handleEliminar = async (id) => {
    if (!confirm('¿Eliminar este cliente?')) return
    try {
      await eliminarCliente(id)
      cargar()
    } catch (err) {
      setError(err.response?.data?.error || 'Error al eliminar')
    }
  }

  return (
    <div className="container py-4">
      <h2 className="mb-4">Clientes</h2>

      {error && (
        <div className="alert alert-danger alert-dismissible">
          {error}
          <button className="btn-close" onClick={() => setError('')} />
        </div>
      )}

      {/* Formulario */}
      <div className="card mb-4">
        <div className="card-header">
          {editando ? 'Editar cliente' : 'Nuevo cliente'}
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
              <label className="form-label">Email</label>
              <input type="email" className="form-control"
                value={form.email}
                onChange={(e) => setForm({ ...form, email: e.target.value })}
                required />
            </div>
            <div className="col-md-4">
              <label className="form-label">Teléfono</label>
              <input type="text" className="form-control"
                value={form.telefono}
                onChange={(e) => setForm({ ...form, telefono: e.target.value })}
                required />
            </div>
            <div className="col-md-6">
              <label className="form-label">Dirección</label>
              <input type="text" className="form-control"
                value={form.direccion}
                onChange={(e) => setForm({ ...form, direccion: e.target.value })}
                required />
            </div>
            <div className="col-md-3">
              <label className="form-label">ID Usuario Web</label>
              <input type="number" className="form-control"
                value={form.usuarioWeb.id}
                onChange={(e) => setForm({ ...form, usuarioWeb: { id: e.target.value } })}
                required />
            </div>
            <div className="col-md-3 d-flex align-items-end gap-2">
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
        <div className="card-body">
          <table className="table table-hover align-middle">
            <thead className="table-dark">
              <tr>
                <th>ID</th>
                <th>Nombre</th>
                <th>Email</th>
                <th>Teléfono</th>
                <th>Dirección</th>
                <th>Usuario Web</th>
                <th>Acciones</th>
              </tr>
            </thead>
            <tbody>
              {clientes.length === 0 ? (
                <tr><td colSpan="7" className="text-center text-muted">Sin clientes</td></tr>
              ) : (
                clientes.map(c => (
                  <tr key={c.id}>
                    <td>{c.id}</td>
                    <td>{c.nombre}</td>
                    <td>{c.email}</td>
                    <td>{c.telefono}</td>
                    <td>{c.direccion}</td>
                    <td>{c.usuarioWeb?.id || '-'}</td>
                    <td className="d-flex gap-2">
                      <button className="btn btn-sm btn-warning"
                        onClick={() => handleEditar(c)}>
                        Editar
                      </button>
                      <button className="btn btn-sm btn-danger"
                        onClick={() => handleEliminar(c.id)}>
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