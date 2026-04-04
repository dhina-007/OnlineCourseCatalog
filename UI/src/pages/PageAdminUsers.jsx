import { useState } from 'react'
import { Navigate } from 'react-router-dom'
import { api, ApiError } from '@/lib/api.js'
import { useAuth } from '@/lib/authStore.js'
import { isAdmin } from '@/lib/roles.js'

const ROLES = ['STUDENT', 'INSTRUCTOR', 'ADMIN']

export function PageAdminUsers() {
  const auth = useAuth()
  const [err, setErr] = useState('')
  const [ok, setOk] = useState('')

  const [cName, setCName] = useState('')
  const [cEmail, setCEmail] = useState('')
  const [cPassword, setCPassword] = useState('')
  const [cRole, setCRole] = useState('STUDENT')

  const [rUserId, setRUserId] = useState('')
  const [rRole, setRRole] = useState('STUDENT')

  if (!auth?.token || !isAdmin(auth.role)) {
    return <Navigate to="/" replace />
  }

  async function createUser(e) {
    e.preventDefault()
    setErr('')
    setOk('')
    try {
      await api('/api/v1/users', {
        method: 'POST',
        body: JSON.stringify({
          name: cName,
          email: cEmail,
          password: cPassword,
          role: cRole,
        }),
      })
      setOk('User created.')
      setCName('')
      setCEmail('')
      setCPassword('')
    } catch (ex) {
      setErr(ex instanceof ApiError ? ex.message : 'Create failed')
    }
  }

  async function changeRole(e) {
    e.preventDefault()
    setErr('')
    setOk('')
    try {
      const id = Number(rUserId)
      if (!id) {
        setErr('Valid user ID required')
        return
      }
      await api(`/api/v1/users/${id}/role`, {
        method: 'PATCH',
        body: JSON.stringify({ role: rRole }),
      })
      setOk('Role updated.')
    } catch (ex) {
      setErr(ex instanceof ApiError ? ex.message : 'Role update failed')
    }
  }

  return (
    <div className="card">
      <h1>Admin · Users</h1>
      {err && <div className="err">{err}</div>}
      {ok && <div className="ok">{ok}</div>}

      <h3>Create user</h3>
      <form onSubmit={createUser}>
        <label>Name</label>
        <input value={cName} onChange={(e) => setCName(e.target.value)} required />
        <label>Email</label>
        <input value={cEmail} onChange={(e) => setCEmail(e.target.value)} type="email" required />
        <label>Password</label>
        <input value={cPassword} onChange={(e) => setCPassword(e.target.value)} type="password" minLength={8} required />
        <label>Role</label>
        <select value={cRole} onChange={(e) => setCRole(e.target.value)}>
          {ROLES.map((r) => (
            <option key={r} value={r}>
              {r}
            </option>
          ))}
        </select>
        <button type="submit">Create</button>
      </form>

      <h3>Change role</h3>
      <form onSubmit={changeRole}>
        <label>User ID</label>
        <input value={rUserId} onChange={(e) => setRUserId(e.target.value)} />
        <label>New role</label>
        <select value={rRole} onChange={(e) => setRRole(e.target.value)}>
          {ROLES.map((r) => (
            <option key={r} value={r}>
              {r}
            </option>
          ))}
        </select>
        <button type="submit">Update role</button>
      </form>
    </div>
  )
}
