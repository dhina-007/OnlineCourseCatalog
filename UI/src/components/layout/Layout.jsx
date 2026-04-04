import { Link, Outlet } from 'react-router-dom'
import { clearAuthSession, useAuth } from '@/lib/authStore.js'
import { apiBlob } from '@/lib/api.js'
import { canEdit, isAdmin } from '@/lib/roles.js'

export function Layout() {
  const auth = useAuth()

  async function downloadReport() {
    try {
      const blob = await apiBlob('/api/v1/reports/courses/pdf')
      const url = URL.createObjectURL(blob)
      const a = document.createElement('a')
      a.href = url
      a.download = 'courses.pdf'
      a.click()
      URL.revokeObjectURL(url)
    } catch (e) {
      alert(e.message || 'Download failed')
    }
  }

  return (
    <>
      <header className="app-header">
        <Link to="/" className="brand">
          CourseCatalog
        </Link>
        <nav>
          <Link to="/">Home</Link>
          <Link to="/courses">Courses</Link>
          {auth?.token && (
            <>
              <Link to="/me/enrollments">My enrollments</Link>
              <Link to="/me">Profile</Link>
            </>
          )}
          {auth?.token && canEdit(auth.role) && (
            <>
              <Link to="/courses/new">New course</Link>
              <button type="button" className="secondary" onClick={downloadReport}>
                Courses PDF
              </button>
            </>
          )}
          {auth?.token && isAdmin(auth.role) && <Link to="/admin/users">Admin users</Link>}
          {!auth?.token ? (
            <>
              <Link to="/login">Login</Link>
              <Link to="/register">Register</Link>
            </>
          ) : (
            <button
              type="button"
              className="secondary"
              onClick={() => {
                clearAuthSession()
              }}
            >
              Log out ({auth.email})
            </button>
          )}
        </nav>
      </header>
      <main className="app-main">
        <Outlet />
      </main>
    </>
  )
}
