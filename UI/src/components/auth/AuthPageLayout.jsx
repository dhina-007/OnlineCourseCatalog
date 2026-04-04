import { Link } from 'react-router-dom'

/**
 * Split auth layout: brand panel + form card (stacks on small screens).
 */
export function AuthPageLayout({ title, lead, children, footer }) {
  return (
    <div className="auth-page">
      <div className="auth-shell">
        <aside className="auth-brand" aria-label="Platform">
          <Link to="/" className="auth-brand__logo">
            <img src="/skillbridge-logo.png" alt="" className="auth-brand__mark" />
            <span>Skillbridge</span>
          </Link>
          <p className="auth-brand__headline">Skills for your present — and your future.</p>
          <p className="auth-brand__copy">
            Join thousands of learners exploring video courses, structured lessons, and clear paths from beginner to
            advanced.
          </p>
          <ul className="auth-brand__list">
            <li>
              <span className="auth-brand__check" aria-hidden />
              Search and filter the full catalog
            </li>
            <li>
              <span className="auth-brand__check" aria-hidden />
              Free and premium courses with secure checkout
            </li>
            <li>
              <span className="auth-brand__check" aria-hidden />
              Track enrollments and lesson progress
            </li>
          </ul>
        </aside>

        <div className="auth-form-column">
          <div className="auth-form-card">
            <header className="auth-form-card__header">
              <h1 className="auth-form-card__title">{title}</h1>
              {lead && <p className="auth-form-card__lead">{lead}</p>}
            </header>
            {children}
            {footer && <footer className="auth-form-card__footer">{footer}</footer>}
          </div>
        </div>
      </div>
    </div>
  )
}
