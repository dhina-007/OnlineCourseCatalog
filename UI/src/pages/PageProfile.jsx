import { useEffect, useState } from 'react'
import { api, ApiError } from '@/lib/api.js'

export function PageProfile() {
  const [user, setUser] = useState(null)
  const [name, setName] = useState('')
  const [email, setEmail] = useState('')
  const [err, setErr] = useState('')
  const [ok, setOk] = useState('')

  useEffect(() => {
    let c = false
    ;(async () => {
      try {
        const me = await api('/api/v1/users/me')
        if (!c) {
          setUser(me)
          setName(me.name || '')
          setEmail(me.email || '')
        }
      } catch (e) {
        if (!c) setErr(e instanceof ApiError ? e.message : 'Failed to load profile')
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
    try {
      const updated = await api(`/api/v1/users/${user.id}`, {
        method: 'PUT',
        body: JSON.stringify({ name, email }),
      })
      setUser(updated)
      setOk('Saved.')
    } catch (ex) {
      setErr(ex instanceof ApiError ? ex.message : 'Save failed')
    }
  }

  if (!user && !err) {
    return <p className="muted">Loading…</p>
  }

  return (
    <div className="card">
      <h1>Profile</h1>
      {err && <div className="err">{err}</div>}
      {ok && <div className="ok">{ok}</div>}
      {user && (
        <form onSubmit={save}>
          <p className="muted">
            Role: <strong>{user.role}</strong>
          </p>
          <label>Name</label>
          <input value={name} onChange={(e) => setName(e.target.value)} required />
          <label>Email</label>
          <input value={email} onChange={(e) => setEmail(e.target.value)} type="email" required />
          <button type="submit">Save</button>
        </form>
      )}
    </div>
  )
}
