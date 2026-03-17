import { useEffect, useState } from 'react'
import { listarAdmins, agregarAdmin, modificarAdmin, eliminarAdmin } from '../api/adminsApi'

const formInicial = {
  nombre: '',
  email: '',
  contrasena: '',
  rol: 'ADMIN'
}

const ROLES = ['SUPER_ADMIN', 'ADMIN', 'MODERADOR']

export default function Admins() {
  const [admins, setAdmins] = useState([])
  const [form, setForm] = useState(formInicial)
  const [editando, setEditando] = useState(null)
  const [error, setError] = useState('')
  const [mostrarContrasena, setMostrarContrasena] = useState(false)

  useEffect(() => { cargar() }, [])

  const cargar = async () => {
    try {
      const res = await listarAdmins()
      setAdmins(res.data)
    } catch {
      setError('Error al cargar administradores')
    }
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    try {
      if (editando) {
        await modificarAdmin({ ...form, id: editando })
      } else {
        await agregarAdmin(form)
      }
      setForm(formInicial)
      setEditando(null)
      cargar()
    } catch (err) {
      setError(err.response?.data?.error || 'Error al guardar')
    }
  }

  const handleEditar = (a) => {
    setEditando(a.id)
    setForm({
      nombre: a.nombre,
      email: a.email,
      contrasena: '',
      rol: a.rol
    })
  }

  const handleEliminar = async (id) => {
    if (!confirm('¿Eliminar este administrador?')) return
    try {
      await eliminarAdmin(id)
      cargar()
    } catch (err) {
      setError(err.response?.data?.error || 'Error al eliminar')
    }
  }

  const badgeRol = (rol) => {
    const colores = {
      SUPER_ADMIN: 'bg-danger',
      ADMIN: 'bg-primary',
      MODERADOR: 'bg-info text-dark'
    }
    return colores[rol] || 'bg-secondary'
  }

  return (
    <div className="container py-4">
      <h2 className="mb-4">Administradores</h2>

      {error && (
        <div className="alert alert-danger alert-dismissible">
          {error}
          <button className="btn-close" onClick={() => setError('')} />
        </div>
      )}

      {/* Formulario */}
      <div className="card mb-4">
        <div className="card-header">
          {editando ? 'Editar administrador' : 'Nuevo administrador'}
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
              <label className="form-label">Rol</label>
              <select className="form-select"
                value={form.rol}
                onChange={(e) => setForm({ ...form, rol: e.target.value })}>
                {ROLES.map(r => (
                  <option key={r} value={r}>{r.replace('_', ' ')}</option>
                ))}
              </select>
            </div>
            <div className="col-md-4">
              <label className="form-label">
                Contraseña {editando && <span className="text-muted">(dejar vacío para no cambiar)</span>}
              </label>
              <div className="input-group">
                <input
                  type={mostrarContrasena ? 'text' : 'password'}
                  className="form-control"
                  value={form.contrasena}
                  onChange={(e) => setForm({ ...form, contrasena: e.target.value })}
                  required={!editando} />
                <button type="button" className="btn btn-outline-secondary"
                  onClick={() => setMostrarContrasena(!mostrarContrasena)}>
                  {mostrarContrasena ? '🙈' : '👁️'}
                </button>
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
                <th>Email</th>
                <th>Rol</th>
                <th>Acciones</th>
              </tr>
            </thead>
            <tbody>
              {admins.length === 0 ? (
                <tr><td colSpan="5" className="text-center text-muted">Sin administradores</td></tr>
              ) : (
                admins.map(a => (
                  <tr key={a.id}>
                    <td>{a.id}</td>
                    <td>{a.nombre}</td>
                    <td>{a.email}</td>
                    <td>
                      <span className={`badge ${badgeRol(a.rol)}`}>
                        {a.rol?.replace('_', ' ')}
                      </span>
                    </td>
                    <td className="d-flex gap-2">
                      <button className="btn btn-sm btn-warning"
                        onClick={() => handleEditar(a)}>
                        Editar
                      </button>
                      <button className="btn btn-sm btn-danger"
                        onClick={() => handleEliminar(a.id)}>
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