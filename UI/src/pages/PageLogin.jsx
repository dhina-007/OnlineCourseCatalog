import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { api, ApiError } from '@/lib/api.js'
import { setAuthSession } from '@/lib/authStore.js'

export function PageLogin() {
  const nav = useNavigate()
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [err, setErr] = useState('')

  async function submit(e) {
    e.preventDefault()
    setErr('')
    try {
      const res = await api('/api/v1/auth/login', {
        method: 'POST',
        body: JSON.stringify({ email, password }),
      })
      setAuthSession({
        token: res.token,
        email: res.email,
        userId: res.userId,
        role: res.role,
      })
      nav('/')
    } catch (ex) {
      setErr(ex instanceof ApiError ? ex.message : 'Login failed')
    }
  }

  return (
    <div className="card card--auth">
      <h2>Login</h2>
      {err && <div className="err">{err}</div>}
      <form onSubmit={submit}>
        <label>Email</label>
        <input value={email} onChange={(e) => setEmail(e.target.value)} type="email" required autoComplete="username" />
        <label>Password</label>
        <input
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          type="password"
          required
          autoComplete="current-password"
        />
        <button type="submit">Sign in</button>
      </form>
      <p className="muted">
        <Link to="/">Home</Link> · <Link to="/courses">Courses</Link> · <Link to="/register">Create account</Link>
      </p>
    </div>
  )
}
