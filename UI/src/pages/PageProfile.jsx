import { useEffect, useMemo, useState } from 'react'
import { Link } from 'react-router-dom'
import { api, ApiError } from '@/lib/api.js'
import { PageLoader, ButtonSpinner } from '@/components/common/Loader.jsx'

function roleLabel(role) {
  if (!role) return ''
  return role.charAt(0) + role.slice(1).toLowerCase()
}

export function PageProfile() {
  const [user, setUser] = useState(null)
  const [name, setName] = useState('')
  const [email, setEmail] = useState('')
  const [err, setErr] = useState('')
  const [ok, setOk] = useState('')
  const [loading, setLoading] = useState(true)
  const [saving, setSaving] = useState(false)

  const initial = useMemo(() => {
    const n = (name || user?.name || '?').trim()
    return n.charAt(0).toUpperCase() || '?'
  }, [name, user?.name])

  useEffect(() => {
    let c = false
    ;(async () => {
      setLoading(true)
      try {
        const me = await api('/api/v1/users/me')
        if (!c) {
          setUser(me)
          setName(me.name || '')
          setEmail(me.email || '')
        }
      } catch (e) {
        if (!c) setErr(e instanceof ApiError ? e.message : 'Failed to load profile')
      } finally {
        if (!c) setLoading(false)
      }
    })()
    return () => {
      c = true
    }
  }, [])

  async function save(e) {
    e.preventDefault()
    setErr('')
    setOk('')
    setSaving(true)
    try {
      const updated = await api(`/api/v1/users/${user.id}`, {
        method: 'PUT',
        body: JSON.stringify({ name, email }),
      })
      setUser(updated)
      setOk('Your profile was updated.')
    } catch (ex) {
      setErr(ex instanceof ApiError ? ex.message : 'Save failed')
    } finally {
      setSaving(false)
    }
  }

  if (loading) {
    return <PageLoader message="Loading profile…" />
  }

  return (
    <div className="page-stack profile-page">
      <header className="page-hero page-hero--compact">
        <h1>Profile</h1>
        <p className="page-hero__lead">Manage your account name and email. Your role is assigned by an administrator.</p>
      </header>

      {err && !user && (
        <div className="card card--soft">
          <div className="err">{err}</div>
        </div>
      )}

      {user && (
        <>
          <div className="card profile-summary-card">
            <div className="profile-summary">
              <div className="profile-avatar" aria-hidden>
                {initial}
              </div>
              <div className="profile-summary__text">
                <h2 className="profile-summary__name">{user.name || 'Your name'}</h2>
                <p className="profile-summary__email">{user.email}</p>
                <span className={`profile-role-badge profile-role-badge--${String(user.role || '').toLowerCase()}`}>
                  {roleLabel(user.role)}
                </span>
              </div>
            </div>
          </div>

          <div className="card profile-form-card">
            <h2 className="profile-form-card__title">Edit details</h2>
            <p className="profile-form-card__hint muted">Changes apply to how your name appears across the platform.</p>
            {err && <div className="err">{err}</div>}
            {ok && <div className="ok profile-save-toast">{ok}</div>}
            <form className="profile-form" onSubmit={save}>
              <div className="auth-field">
                <label htmlFor="profile-name">Full name</label>
                <input
                  id="profile-name"
                  value={name}
                  onChange={(e) => setName(e.target.value)}
                  required
                  disabled={saving}
                  autoComplete="name"
                  placeholder="Your name"
                />
              </div>
              <div className="auth-field">
                <label htmlFor="profile-email">Email</label>
                <input
                  id="profile-email"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  type="email"
                  required
                  disabled={saving}
                  autoComplete="email"
                  placeholder="you@example.com"
                />
              </div>
              <div className="profile-form__actions">
                <button type="submit" className="btn profile-save-btn" disabled={saving}>
                  <ButtonSpinner busy={saving} busyLabel="Saving…">
                    Save changes
                  </ButtonSpinner>
                </button>
                <Link to="/courses" className="btn secondary profile-link-catalog">
                  Browse courses
                </Link>
              </div>
            </form>
          </div>
        </>
      )}
    </div>
  )
}
