import { NavLink } from 'react-router-dom'

const links = [
  { to: '/dashboard', label: '📊 Dashboard' },
  { to: '/clientes', label: '👥 Clientes' },
  { to: '/productos', label: '📦 Productos' },
  { to: '/categorias', label: '🗂️ Categorías' },
  { to: '/pedidos', label: '🛒 Pedidos' },
  { to: '/pagos', label: '💳 Pagos' },
  { to: '/admins', label: '🔧 Admins' },
]

function Sidebar() {
  return (
    <div className="bg-dark text-white d-flex flex-column p-3" style={{ width: '220px', minHeight: '100vh' }}>
      <h5 className="text-center mb-4 mt-2">🛍️ Sistema Compras</h5>
      <nav className="d-flex flex-column gap-1">
        {links.map(link => (
          <NavLink
            key={link.to}
            to={link.to}
            className={({ isActive }) =>
              `text-decoration-none px-3 py-2 rounded ${isActive ? 'bg-primary text-white' : 'text-white-50'}`
            }
          >
            {link.label}
          </NavLink>
        ))}
      </nav>
    </div>
  )
}

export default Sidebar