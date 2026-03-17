import { Outlet } from 'react-router-dom'
import Sidebar from './Sidebar'
import Navbar from './Navbar'

function Layout() {
  return (
    <div className="d-flex" style={{ minHeight: '100vh' }}>
      <Sidebar />
      <div className="flex-grow-1 d-flex flex-column">
        <Navbar />
        <main className="p-4 bg-light flex-grow-1">
          <Outlet />
        </main>
      </div>
    </div>
  )
}

export default Layout