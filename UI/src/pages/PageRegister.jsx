import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { api, ApiError } from '@/lib/api.js'
import { setAuthSession } from '@/lib/authStore.js'
import { ButtonSpinner } from '@/components/common/Loader.jsx'
import { AuthPageLayout } from '@/components/auth/AuthPageLayout.jsx'

export function PageRegister() {
  const nav = useNavigate()
  const [name, setName] = useState('')
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [err, setErr] = useState('')
  const [submitting, setSubmitting] = useState(false)

  async function submit(e) {
    e.preventDefault()
    setErr('')
    setSubmitting(true)
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
    } finally {
      setSubmitting(false)
    }
  }

  const footer = (
    <>
      <p className="auth-alt-action">
        Already have an account?{' '}
        <Link to="/login" className="auth-alt-action__link">
          Log in
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
      title="Create your account"
      lead="Start learning in minutes. No credit card required to browse free courses."
      footer={footer}
    >
      {err && (
        <div className="auth-alert auth-alert--error" role="alert">
          {err}
        </div>
      )}
      <form className="auth-form" onSubmit={submit} noValidate>
        <div className="auth-field">
          <label htmlFor="register-name">Full name</label>
          <input
            id="register-name"
            value={name}
            onChange={(e) => setName(e.target.value)}
            required
            disabled={submitting}
            autoComplete="name"
            placeholder="Jane Doe"
          />
        </div>
        <div className="auth-field">
          <label htmlFor="register-email">Email</label>
          <input
            id="register-email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            type="email"
            required
            autoComplete="email"
            disabled={submitting}
            placeholder="you@example.com"
          />
        </div>
        <div className="auth-field">
          <label htmlFor="register-password">Password</label>
          <input
            id="register-password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            type="password"
            required
            minLength={8}
            autoComplete="new-password"
            disabled={submitting}
            placeholder="At least 8 characters"
          />
          <p className="auth-field__hint">Use 8 or more characters with a mix of letters and numbers.</p>
        </div>
        <button type="submit" className="auth-submit btn" disabled={submitting}>
          <ButtonSpinner busy={submitting} busyLabel="Creating account…">
            Sign up
          </ButtonSpinner>
        </button>
        <p className="auth-legal">
          By signing up, you agree to our learning platform terms and acknowledge our focus on education and fair use.
        </p>
      </form>
    </AuthPageLayout>
  )
}
