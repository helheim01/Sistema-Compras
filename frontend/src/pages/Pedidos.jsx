import { useEffect, useState } from 'react'
import { listarPedidos, agregarPedido, modificarPedido, eliminarPedido } from '../api/pedidosApi'
import { listarClientes } from '../api/clienteApi'

const formInicial = {
  fechaPedido: '',
  estado: 'PENDIENTE',
  total: '',
  cliente: { id: '' }
}

const ESTADOS = ['PENDIENTE', 'PROCESANDO', 'ENVIADO', 'ENTREGADO', 'CANCELADO']

export default function Pedidos() {
  const [pedidos, setPedidos] = useState([])
  const [clientes, setClientes] = useState([])
  const [form, setForm] = useState(formInicial)
  const [editando, setEditando] = useState(null)
  const [error, setError] = useState('')

  useEffect(() => {
    cargar()
    cargarClientes()
  }, [])

  const cargar = async () => {
    try {
      const res = await listarPedidos()
      setPedidos(res.data)
    } catch {
      setError('Error al cargar pedidos')
    }
  }

  const cargarClientes = async () => {
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
        total: parseFloat(form.total),
        cliente: { id: parseInt(form.cliente.id) }
      }
      if (editando) {
        await modificarPedido({ ...payload, id: editando })
      } else {
        await agregarPedido(payload)
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
      fechaPedido: p.fechaPedido?.substring(0, 16) || '',
      estado: p.estado,
      total: p.total,
      cliente: { id: p.cliente?.id || '' }
    })
  }

  const handleEliminar = async (id) => {
    if (!confirm('¿Eliminar este pedido?')) return
    try {
      await eliminarPedido(id)
      cargar()
    } catch (err) {
      setError(err.response?.data?.error || 'Error al eliminar')
    }
  }

  const badgeEstado = (estado) => {
    const colores = {
      PENDIENTE: 'bg-warning text-dark',
      PROCESANDO: 'bg-info text-dark',
      ENVIADO: 'bg-primary',
      ENTREGADO: 'bg-success',
      CANCELADO: 'bg-danger'
    }
    return colores[estado] || 'bg-secondary'
  }

  return (
    <div className="container py-4">
      <h2 className="mb-4">Pedidos</h2>

      {error && (
        <div className="alert alert-danger alert-dismissible">
          {error}
          <button className="btn-close" onClick={() => setError('')} />
        </div>
      )}

      {/* Formulario */}
      <div className="card mb-4">
        <div className="card-header">
          {editando ? 'Editar pedido' : 'Nuevo pedido'}
        </div>
        <div className="card-body">
          <form onSubmit={handleSubmit} className="row g-3">
            <div className="col-md-4">
              <label className="form-label">Cliente</label>
              <select className="form-select"
                value={form.cliente.id}
                onChange={(e) => setForm({ ...form, cliente: { id: e.target.value } })}
                required>
                <option value="">Seleccionar...</option>
                {clientes.map(c => (
                  <option key={c.id} value={c.id}>{c.nombre} — {c.email}</option>
                ))}
              </select>
            </div>
            <div className="col-md-3">
              <label className="form-label">Fecha</label>
              <input type="datetime-local" className="form-control"
                value={form.fechaPedido}
                onChange={(e) => setForm({ ...form, fechaPedido: e.target.value })}
                required />
            </div>
            <div className="col-md-2">
              <label className="form-label">Total</label>
              <input type="number" step="0.01" className="form-control"
                value={form.total}
                onChange={(e) => setForm({ ...form, total: e.target.value })}
                required />
            </div>
            <div className="col-md-3">
              <label className="form-label">Estado</label>
              <select className="form-select"
                value={form.estado}
                onChange={(e) => setForm({ ...form, estado: e.target.value })}>
                {ESTADOS.map(e => (
                  <option key={e} value={e}>{e}</option>
                ))}
              </select>
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
                <th>Cliente</th>
                <th>Fecha</th>
                <th>Total</th>
                <th>Estado</th>
                <th>Acciones</th>
              </tr>
            </thead>
            <tbody>
              {pedidos.length === 0 ? (
                <tr><td colSpan="6" className="text-center text-muted">Sin pedidos</td></tr>
              ) : (
                pedidos.map(p => (
                  <tr key={p.id}>
                    <td>{p.id}</td>
                    <td>{p.cliente?.nombre || '-'}</td>
                    <td>{p.fechaPedido ? new Date(p.fechaPedido).toLocaleString('es-AR') : '-'}</td>
                    <td>${p.total?.toFixed(2)}</td>
                    <td>
                      <span className={`badge ${badgeEstado(p.estado)}`}>
                        {p.estado}
                      </span>
                    </td>
                    <td className="d-flex gap-2">
                      <button className="btn btn-sm btn-warning"
                        onClick={() => handleEditar(p)}>
                        Editar
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