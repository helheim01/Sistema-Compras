import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import Layout from './components/Layout'
import Dashboard from './pages/Dashboard'
import Clientes from './pages/Clientes'
import Productos from './pages/Productos'
import Categorias from './pages/Categorias'
import Pedidos from './pages/Pedidos'
import Pagos from './pages/Pagos'
import Admins from './pages/Admins'

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Layout />}>
          <Route index element={<Navigate to="/dashboard" />} />
          <Route path="dashboard" element={<Dashboard />} />
          <Route path="clientes" element={<Clientes />} />
          <Route path="productos" element={<Productos />} />
          <Route path="categorias" element={<Categorias />} />
          <Route path="pedidos" element={<Pedidos />} />
          <Route path="pagos" element={<Pagos />} />
          <Route path="admins" element={<Admins />} />
        </Route>
      </Routes>
    </BrowserRouter>
  )
}

export default App