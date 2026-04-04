import { Link } from 'react-router-dom'
import { useAuth } from '@/lib/authStore.js'

export function PageLanding() {
  const auth = useAuth()

  return (
    <div className="landing">
      <section className="landing-hero landing-hero--split" aria-labelledby="landing-heading">
        <div className="landing-hero__copy">
          <p className="landing-eyebrow">Online learning that fits your life</p>
          <h1 id="landing-heading">Skills for your present — and your future.</h1>
          <p className="landing-lead">
            Learn from real instructors with video courses, structured lessons, and materials you can access anytime.
            Free courses open to everyone; premium paths unlock after a simple checkout.
          </p>
          <div className="landing-cta-row">
            <Link to="/courses" className="btn landing-cta-primary">
              Explore courses
            </Link>
            {!auth?.token && (
              <>
                <Link to="/register" className="btn btn--outline landing-cta-secondary">
                  Get started — it&apos;s free
                </Link>
                <Link to="/login" className="btn btn--ghost">
                  Log in
                </Link>
              </>
            )}
            {auth?.token && (
              <Link to="/courses" className="btn btn--outline landing-cta-secondary">
                Continue to catalog
              </Link>
            )}
          </div>
          <ul className="landing-stats" aria-label="Platform highlights">
            <li>
              <strong>Video-first</strong>
              <span>Promo &amp; lesson playback</span>
            </li>
            <li>
              <strong>Flexible</strong>
              <span>Learn at your pace</span>
            </li>
            <li>
              <strong>Secure</strong>
              <span>Trusted checkout flow</span>
            </li>
          </ul>
        </div>
        <div className="landing-hero__visual" aria-hidden>
          <div className="landing-mock-card landing-mock-card--1">
            <span className="landing-mock-pill">Featured</span>
            <span className="landing-mock-title">Full-stack foundations</span>
            <span className="landing-mock-meta">4.8 ★ · 12 lessons</span>
          </div>
          <div className="landing-mock-card landing-mock-card--2">
            <span className="landing-mock-pill landing-mock-pill--teal">New</span>
            <span className="landing-mock-title">Spring Boot APIs</span>
            <span className="landing-mock-meta">Project-based · Certificate</span>
          </div>
          <div className="landing-mock-card landing-mock-card--3">
            <span className="landing-mock-play" />
            <span className="landing-mock-title">Watch anywhere</span>
          </div>
        </div>
      </section>

      <section className="landing-trust" aria-label="Trusted experience">
        <p className="landing-trust__label">Built for clarity and speed</p>
        <div className="landing-trust__row">
          <span>Catalog search</span>
          <span className="landing-trust__dot" />
          <span>Enrollments &amp; progress</span>
          <span className="landing-trust__dot" />
          <span>Instructor tools</span>
        </div>
      </section>

      <section className="landing-features" aria-label="Why learners choose us">
        <div className="landing-feature">
          <div className="landing-feature__icon landing-feature__icon--play" aria-hidden />
          <h3>Learn in-demand skills</h3>
          <p>Structured paths from fundamentals to deeper topics — with ratings and clear pricing.</p>
        </div>
        <div className="landing-feature">
          <div className="landing-feature__icon landing-feature__icon--star" aria-hidden />
          <h3>Real course structure</h3>
          <p>Lessons, materials, and reviews mirror how serious platforms organize content.</p>
        </div>
        <div className="landing-feature">
          <div className="landing-feature__icon landing-feature__icon--clock" aria-hidden />
          <h3>Your schedule</h3>
          <p>Free previews where allowed; paid courses unlock the full experience after purchase.</p>
        </div>
      </section>

      <section className="landing-cta-band" aria-labelledby="landing-cta-end">
        <div className="landing-cta-band__inner">
          <h2 id="landing-cta-end">Ready to start learning?</h2>
          <p>Browse the catalog, pick a course, and enroll in minutes.</p>
          <Link to="/courses" className="btn landing-cta-primary">
            View all courses
          </Link>
        </div>
      </section>
    </div>
  )
}
