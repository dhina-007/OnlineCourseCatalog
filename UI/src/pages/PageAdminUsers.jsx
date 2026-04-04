import { useState } from 'react'
import { Navigate } from 'react-router-dom'
import { api, ApiError } from '@/lib/api.js'
import { useAuth } from '@/lib/authStore.js'
import { isAdmin } from '@/lib/roles.js'
import { ButtonSpinner } from '@/components/common/Loader.jsx'

const ROLES = ['STUDENT', 'INSTRUCTOR', 'ADMIN']

export function PageAdminUsers() {
  const auth = useAuth()
  const [err, setErr] = useState('')
  const [ok, setOk] = useState('')
  const [creating, setCreating] = useState(false)
  const [updatingRole, setUpdatingRole] = useState(false)

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
    setCreating(true)
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
    } finally {
      setCreating(false)
    }
  }

  async function changeRole(e) {
    e.preventDefault()
    setErr('')
    setOk('')
    setUpdatingRole(true)
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
    } finally {
      setUpdatingRole(false)
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
        <input value={cName} onChange={(e) => setCName(e.target.value)} required disabled={creating} />
        <label>Email</label>
        <input value={cEmail} onChange={(e) => setCEmail(e.target.value)} type="email" required disabled={creating} />
        <label>Password</label>
        <input
          value={cPassword}
          onChange={(e) => setCPassword(e.target.value)}
          type="password"
          minLength={8}
          required
          disabled={creating}
        />
        <label>Role</label>
        <select value={cRole} onChange={(e) => setCRole(e.target.value)} disabled={creating}>
          {ROLES.map((r) => (
            <option key={r} value={r}>
              {r}
            </option>
          ))}
        </select>
        <button type="submit" disabled={creating}>
          <ButtonSpinner busy={creating} busyLabel="Creating…">
            Create
          </ButtonSpinner>
        </button>
      </form>

      <h3>Change role</h3>
      <form onSubmit={changeRole}>
        <label>User ID</label>
        <input value={rUserId} onChange={(e) => setRUserId(e.target.value)} disabled={updatingRole} />
        <label>New role</label>
        <select value={rRole} onChange={(e) => setRRole(e.target.value)} disabled={updatingRole}>
          {ROLES.map((r) => (
            <option key={r} value={r}>
              {r}
            </option>
          ))}
        </select>
        <button type="submit" disabled={updatingRole}>
          <ButtonSpinner busy={updatingRole} busyLabel="Updating…">
            Update role
          </ButtonSpinner>
        </button>
      </form>
    </div>
  )
}
