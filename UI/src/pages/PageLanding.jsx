import { Link } from 'react-router-dom'
import { useAuth } from '@/lib/authStore.js'

export function PageLanding() {
  const auth = useAuth()

  return (
    <div className="landing">
      <section className="landing-hero" aria-labelledby="landing-heading">
        <h1 id="landing-heading">Learn without limits</h1>
        <p className="landing-lead">
          Start, switch, or advance your career with video courses from real instructors — browse the catalog, enroll, and learn at your pace.
        </p>
        <div className="landing-cta-row">
          <Link to="/courses" className="btn landing-cta-primary">
            Explore courses
          </Link>
          {!auth?.token && (
            <>
              <Link to="/register" className="btn secondary landing-cta-secondary">
                Get started
              </Link>
              <Link to="/login" className="btn secondary">
                Log in
              </Link>
            </>
          )}
        </div>
      </section>

      <section className="landing-features" aria-label="Why choose us">
        <div className="landing-feature">
          <h3>Learn in-demand skills</h3>
          <p>Structured courses with clear outcomes — from fundamentals to deeper topics.</p>
        </div>
        <div className="landing-feature">
          <h3>Learn on your schedule</h3>
          <p>Access materials anytime. Free courses are open to everyone; premium courses unlock after purchase.</p>
        </div>
        <div className="landing-feature">
          <h3>Trusted checkout</h3>
          <p>Paid courses use a straightforward purchase flow so you can enroll and start learning quickly.</p>
        </div>
      </section>
    </div>
  )
}
