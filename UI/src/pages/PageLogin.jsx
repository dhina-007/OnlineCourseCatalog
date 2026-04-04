import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { api, ApiError } from '@/lib/api.js'
import { setAuthSession } from '@/lib/authStore.js'
import { ButtonSpinner } from '@/components/common/Loader.jsx'
import { AuthPageLayout } from '@/components/auth/AuthPageLayout.jsx'

export function PageLogin() {
  const nav = useNavigate()
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [err, setErr] = useState('')
  const [submitting, setSubmitting] = useState(false)

  async function submit(e) {
    e.preventDefault()
    setErr('')
    setSubmitting(true)
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
    } finally {
      setSubmitting(false)
    }
  }

  const footer = (
    <>
      <p className="auth-alt-action">
        Don&apos;t have an account?{' '}
        <Link to="/register" className="auth-alt-action__link">
          Sign up
        </Link>
      </p>
      <nav className="auth-nav-links" aria-label="Quick links">
        <Link to="/">Home</Link>
        <Link to="/courses">Courses</Link>
      </nav>
    </>
  )

  return (
    <AuthPageLayout
      title="Welcome back"
      lead="Log in to continue learning and manage your enrollments."
      footer={footer}
    >
      {err && (
        <div className="auth-alert auth-alert--error" role="alert">
          {err}
        </div>
      )}
      <form className="auth-form" onSubmit={submit} noValidate>
        <div className="auth-field">
          <label htmlFor="login-email">Email</label>
          <input
            id="login-email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            type="email"
            required
            autoComplete="username"
            disabled={submitting}
            placeholder="you@example.com"
          />
        </div>
        <div className="auth-field">
          <label htmlFor="login-password">Password</label>
          <input
            id="login-password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            type="password"
            required
            autoComplete="current-password"
            disabled={submitting}
            placeholder="Enter your password"
          />
        </div>
        <button type="submit" className="auth-submit btn" disabled={submitting}>
          <ButtonSpinner busy={submitting} busyLabel="Signing in…">
            Log in
          </ButtonSpinner>
        </button>
      </form>
    </AuthPageLayout>
  )
}
