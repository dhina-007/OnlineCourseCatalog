import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { api, ApiError } from '@/lib/api.js'
import { setAuthSession } from '@/lib/authStore.js'

export function PageRegister() {
  const nav = useNavigate()
  const [name, setName] = useState('')
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [err, setErr] = useState('')

  async function submit(e) {
    e.preventDefault()
    setErr('')
    try {
      const res = await api('/api/v1/auth/register', {
        method: 'POST',
        body: JSON.stringify({ name, email, password }),
      })
      setAuthSession({
        token: res.token,
        email: res.email,
        userId: res.userId,
        role: res.role,
      })
      nav('/')
    } catch (ex) {
      setErr(ex instanceof ApiError ? ex.message : 'Registration failed')
    }
  }

  return (
    <div className="card card--auth">
      <h2>Register</h2>
      {err && <div className="err">{err}</div>}
      <form onSubmit={submit}>
        <label>Name</label>
        <input value={name} onChange={(e) => setName(e.target.value)} required />
        <label>Email</label>
        <input value={email} onChange={(e) => setEmail(e.target.value)} type="email" required autoComplete="email" />
        <label>Password (min 8)</label>
        <input
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          type="password"
          required
          minLength={8}
          autoComplete="new-password"
        />
        <button type="submit">Create account</button>
      </form>
      <p className="muted">
        <Link to="/">Home</Link> · <Link to="/courses">Courses</Link> · <Link to="/login">Login</Link>
      </p>
    </div>
  )
}
