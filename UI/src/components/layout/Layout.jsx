import { useEffect, useRef, useState } from 'react'
import { Link, Outlet } from 'react-router-dom'
import { clearAuthSession, useAuth } from '@/lib/authStore.js'
import { apiBlob } from '@/lib/api.js'
import { canEdit, isAdmin } from '@/lib/roles.js'
import { ButtonSpinner } from '@/components/common/Loader.jsx'

export function Layout() {
  const auth = useAuth()
  const [reportDownloading, setReportDownloading] = useState(false)
  const [navOpen, setNavOpen] = useState(false)
  const [userMenuOpen, setUserMenuOpen] = useState(false)
  const userMenuRef = useRef(null)

  useEffect(() => {
    if (!navOpen) return
    function onKey(e) {
      if (e.key === 'Escape') setNavOpen(false)
    }
    document.documentElement.classList.add('nav-drawer-active')
    window.addEventListener('keydown', onKey)
    return () => {
      window.removeEventListener('keydown', onKey)
      document.documentElement.classList.remove('nav-drawer-active')
    }
  }, [navOpen])

  function closeNav() {
    setNavOpen(false)
    setUserMenuOpen(false)
  }

  useEffect(() => {
    if (!userMenuOpen) return
    function onDocMouseDown(e) {
      if (userMenuRef.current && !userMenuRef.current.contains(e.target)) {
        setUserMenuOpen(false)
      }
    }
    function onKey(e) {
      if (e.key === 'Escape') setUserMenuOpen(false)
    }
    document.addEventListener('mousedown', onDocMouseDown)
    document.addEventListener('keydown', onKey)
    return () => {
      document.removeEventListener('mousedown', onDocMouseDown)
      document.removeEventListener('keydown', onKey)
    }
  }, [userMenuOpen])

  async function downloadReport() {
    setReportDownloading(true)
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
    } finally {
      setReportDownloading(false)
    }
  }

  return (
    <>
      <header className="app-header">
        <Link to="/" className="brand" onClick={closeNav}>
          <img src="/skillbridge-logo.png" alt="" className="brand__mark" />
          <span className="brand__name">Skillbridge</span>
        </Link>
        <button
          type="button"
          className="app-header__menu-toggle"
          aria-label={navOpen ? 'Close menu' : 'Open menu'}
          aria-expanded={navOpen}
          aria-controls="main-navigation"
          onClick={() => setNavOpen((o) => !o)}
        >
          <span className={`app-header__menu-bars ${navOpen ? 'is-open' : ''}`} aria-hidden />
        </button>
        <div
          className={`app-header__backdrop ${navOpen ? 'is-visible' : ''}`}
          aria-hidden
          onClick={closeNav}
        />
        <nav
          id="main-navigation"
          className={`app-header__nav ${navOpen ? 'is-open' : ''}`}
          aria-label="Main"
        >
          <Link to="/" onClick={closeNav}>
            Home
          </Link>
          <Link to="/courses" onClick={closeNav}>
            Courses
          </Link>
          {auth?.token && (
            <>
              <Link to="/me/enrollments" onClick={closeNav}>
                My enrollments
              </Link>
              <Link to="/me" onClick={closeNav}>
                Profile
              </Link>
            </>
          )}
          {auth?.token && canEdit(auth.role) && (
            <>
              <Link to="/courses/new" onClick={closeNav}>
                New course
              </Link>
              <button type="button" className="secondary" onClick={downloadReport} disabled={reportDownloading}>
                <ButtonSpinner busy={reportDownloading} busyLabel="Downloading…">
                  Courses PDF
                </ButtonSpinner>
              </button>
            </>
          )}
          {auth?.token && isAdmin(auth.role) && (
            <Link to="/admin/users" onClick={closeNav}>
              Admin users
            </Link>
          )}
          {!auth?.token ? (
            <>
              <Link to="/login" onClick={closeNav}>
                Login
              </Link>
              <Link to="/register" onClick={closeNav}>
                Register
              </Link>
            </>
          ) : (
            <div className="app-header__user-menu" ref={userMenuRef}>
              <button
                type="button"
                className="app-header__user-trigger"
                aria-expanded={userMenuOpen}
                aria-haspopup="true"
                aria-controls="user-account-menu"
                id="user-account-trigger"
                onClick={() => setUserMenuOpen((o) => !o)}
              >
                <span className="app-header__user-email">{auth.email}</span>
                <span className={`app-header__user-chevron ${userMenuOpen ? 'is-open' : ''}`} aria-hidden />
              </button>
              {userMenuOpen && (
                <div
                  id="user-account-menu"
                  className="app-header__user-dropdown"
                  role="menu"
                  aria-labelledby="user-account-trigger"
                >
                  <button
                    type="button"
                    className="app-header__user-dropdown-item"
                    role="menuitem"
                    onClick={() => {
                      clearAuthSession()
                      setUserMenuOpen(false)
                      closeNav()
                    }}
                  >
                    Log out
                  </button>
                </div>
              )}
            </div>
          )}
        </nav>
      </header>
      <main className="app-main">
        <Outlet />
      </main>
    </>
  )
}
