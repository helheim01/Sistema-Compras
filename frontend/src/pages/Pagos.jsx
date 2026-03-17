import { useEffect, useState } from 'react'
import { listarPagos, agregarPago, modificarPago, eliminarPago } from '../api/pagosApi'
import { listarPedidos } from '../api/pedidosApi'

const formInicial = {
  importe: '',
  tipoPago: 'TARJETA_CREDITO',
  estado: 'PENDIENTE',
  detalle: '',
  pedido: { id: '' },
  cuenta: { id: '' },
  metodoPago: { tipo: 'TARJETA', id: '' }
}

const TIPOS_PAGO = ['TARJETA_CREDITO', 'TARJETA_DEBITO', 'PUNTOS_RECOMPENSA', 'PAYPAL', 'TRANSFERENCIA']
const ESTADOS_PAGO = ['PENDIENTE', 'COMPLETADO', 'FALLIDO', 'REEMBOLSADO']

export default function Pagos() {
  const [pagos, setPagos] = useState([])
  const [pedidos, setPedidos] = useState([])
  const [form, setForm] = useState(formInicial)
  const [editando, setEditando] = useState(null)
  const [error, setError] = useState('')

  useEffect(() => {
    cargar()
    cargarPedidos()
  }, [])

  const cargar = async () => {
    try {
      const res = await listarPagos()
      setPagos(res.data)
    } catch {
      setError('Error al cargar pagos')
    }
  }

  const cargarPedidos = async () => {
    try {
      const res = await listarPedidos()
      setPedidos(res.data)
    } catch {
      setError('Error al cargar pedidos')
    }
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    try {
      const payload = {
        ...form,
        importe: parseFloat(form.importe),
        pedido: { id: parseInt(form.pedido.id) },
        cuenta: { id: parseInt(form.cuenta.id) },
        metodoPago: { tipo: form.metodoPago.tipo, id: parseInt(form.metodoPago.id) }
      }
      if (editando) {
        await modificarPago({ ...payload, id: editando })
      } else {
        await agregarPago(payload)
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
      importe: p.importe,
      tipoPago: p.tipoPago,
      estado: p.estado,
      detalle: p.detalle || '',
      pedido: { id: p.pedido?.id || '' },
      cuenta: { id: p.cuenta?.id || '' },
      metodoPago: { tipo: p.metodoPago?.tipo || 'TARJETA', id: p.metodoPago?.id || '' }
    })
  }

  const handleEliminar = async (id) => {
    if (!confirm('¿Eliminar este pago?')) return
    try {
      await eliminarPago(id)
      cargar()
    } catch (err) {
      setError(err.response?.data?.error || 'Error al eliminar')
    }
  }

  const badgeEstado = (estado) => {
    const colores = {
      PENDIENTE: 'bg-warning text-dark',
      COMPLETADO: 'bg-success',
      FALLIDO: 'bg-danger',
      REEMBOLSADO: 'bg-info text-dark'
    }
    return colores[estado] || 'bg-secondary'
  }

  return (
    <div className="container py-4">
      <h2 className="mb-4">Pagos</h2>

      {error && (
        <div className="alert alert-danger alert-dismissible">
          {error}
          <button className="btn-close" onClick={() => setError('')} />
        </div>
      )}

      {/* Formulario */}
      <div className="card mb-4">
        <div className="card-header">
          {editando ? 'Editar pago' : 'Nuevo pago'}
        </div>
        <div className="card-body">
          <form onSubmit={handleSubmit} className="row g-3">
            <div className="col-md-4">
              <label className="form-label">Pedido</label>
              <select className="form-select"
                value={form.pedido.id}
                onChange={(e) => setForm({ ...form, pedido: { id: e.target.value } })}
                required>
                <option value="">Seleccionar...</option>
                {pedidos.map(p => (
                  <option key={p.id} value={p.id}>
                    #{p.id} — {p.cliente?.nombre} (${p.total?.toFixed(2)})
                  </option>
                ))}
              </select>
            </div>
            <div className="col-md-2">
              <label className="form-label">ID Cuenta</label>
              <input type="number" className="form-control"
                value={form.cuenta.id}
                onChange={(e) => setForm({ ...form, cuenta: { id: e.target.value } })}
                required />
            </div>
            <div className="col-md-2">
              <label className="form-label">Importe</label>
              <input type="number" step="0.01" className="form-control"
                value={form.importe}
                onChange={(e) => setForm({ ...form, importe: e.target.value })}
                required />
            </div>
            <div className="col-md-4">
              <label className="form-label">Tipo de pago</label>
              <select className="form-select"
                value={form.tipoPago}
                onChange={(e) => setForm({ ...form, tipoPago: e.target.value })}>
                {TIPOS_PAGO.map(t => (
                  <option key={t} value={t}>{t.replace('_', ' ')}</option>
                ))}
              </select>
            </div>
            <div className="col-md-3">
              <label className="form-label">Estado</label>
              <select className="form-select"
                value={form.estado}
                onChange={(e) => setForm({ ...form, estado: e.target.value })}>
                {ESTADOS_PAGO.map(e => (
                  <option key={e} value={e}>{e}</option>
                ))}
              </select>
            </div>
            <div className="col-md-2">
              <label className="form-label">Tipo método pago</label>
              <select className="form-select"
                value={form.metodoPago.tipo}
                onChange={(e) => setForm({ ...form, metodoPago: { ...form.metodoPago, tipo: e.target.value } })}>
                <option value="TARJETA">TARJETA</option>
                <option value="PUNTOS">PUNTOS</option>
              </select>
            </div>
            <div className="col-md-2">
              <label className="form-label">ID Método pago</label>
              <input type="number" className="form-control"
                value={form.metodoPago.id}
                onChange={(e) => setForm({ ...form, metodoPago: { ...form.metodoPago, id: e.target.value } })}
                required />
            </div>
            <div className="col-md-5">
              <label className="form-label">Detalle</label>
              <input type="text" className="form-control"
                value={form.detalle}
                onChange={(e) => setForm({ ...form, detalle: e.target.value })} />
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
                <th>Código</th>
                <th>Pedido</th>
                <th>Importe</th>
                <th>Tipo</th>
                <th>Estado</th>
                <th>Fecha</th>
                <th>Acciones</th>
              </tr>
            </thead>
            <tbody>
              {pagos.length === 0 ? (
                <tr><td colSpan="8" className="text-center text-muted">Sin pagos</td></tr>
              ) : (
                pagos.map(p => (
                  <tr key={p.id}>
                    <td>{p.id}</td>
                    <td><code>{p.codigoTransaccion}</code></td>
                    <td>#{p.pedido?.id} — {p.pedido?.cliente?.nombre || '-'}</td>
                    <td>${p.importe?.toFixed(2)}</td>
                    <td>{p.tipoPago?.replace('_', ' ')}</td>
                    <td>
                      <span className={`badge ${badgeEstado(p.estado)}`}>
                        {p.estado}
                      </span>
                    </td>
                    <td>{p.fechaPago ? new Date(p.fechaPago).toLocaleString('es-AR') : '-'}</td>
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