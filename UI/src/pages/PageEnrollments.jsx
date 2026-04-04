import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { api, ApiError } from '@/lib/api.js'
import { PageLoader } from '@/components/common/Loader.jsx'

export function PageEnrollments() {
  const [list, setList] = useState([])
  const [err, setErr] = useState('')
  const [loading, setLoading] = useState(true)
  const [coursesById, setCoursesById] = useState({})
  const [metaLoading, setMetaLoading] = useState(false)

  useEffect(() => {
    let c = false
    ;(async () => {
      setLoading(true)
      try {
        const res = await api('/api/v1/users/me/enrollments')
        if (!c) setList(res || [])
      } catch (e) {
        if (!c) setErr(e instanceof ApiError ? e.message : 'Failed to load')
      } finally {
        if (!c) setLoading(false)
      }
    })()
    return () => {
      c = true
    }
  }, [])

  useEffect(() => {
    const ids = [...new Set((list || []).map((e) => e.courseId))]
    if (ids.length === 0) {
      setCoursesById({})
      setMetaLoading(false)
      return
    }
    let cancelled = false
    setMetaLoading(true)
    ;(async () => {
      try {
        const results = await Promise.all(
          ids.map(async (id) => {
            try {
              const course = await api(`/api/v1/courses/${id}`)
              return [id, course]
            } catch {
              return [id, null]
            }
          }),
        )
        if (cancelled) return
        const map = {}
        results.forEach(([id, course]) => {
          if (course) map[id] = course
        })
        setCoursesById(map)
      } finally {
        if (!cancelled) setMetaLoading(false)
      }
    })()
    return () => {
      cancelled = true
    }
  }, [list])

  if (loading) {
    return <PageLoader message="Loading enrollments…" />
  }

  return (
    <div className="page-stack">
      <header className="page-hero page-hero--compact">
        <h1>My learning</h1>
        <p className="page-hero__lead">Courses you&apos;re enrolled in — open any course to continue where you left off.</p>
      </header>

      {err && (
        <div className="card card--soft">
          <div className="err">{err}</div>
        </div>
      )}

      {!err && list.length === 0 && (
        <div className="empty-state card">
          <div className="empty-state__icon" aria-hidden />
          <h2 className="empty-state__title">No enrollments yet</h2>
          <p className="empty-state__text">Browse the catalog and join a free course, or purchase a premium course to get started.</p>
          <Link to="/courses" className="btn empty-state__cta">
            Explore courses
          </Link>
        </div>
      )}

      {!err && list.length > 0 && (
        <>
          {metaLoading && <p className="muted enrollment-meta-hint">Loading course details…</p>}
          <ul className="enrollment-list">
            {list.map((e) => {
              const course = coursesById[e.courseId]
              const title = course?.title || `Course #${e.courseId}`
              return (
                <li key={e.id}>
                  <article className="enrollment-card">
                    <Link to={`/courses/${e.courseId}`} className="enrollment-card__media" aria-hidden={!course?.imageUrl}>
                      {course?.imageUrl ? (
                        <img src={course.imageUrl} alt="" loading="lazy" decoding="async" />
                      ) : (
                        <div className="enrollment-card__media-fallback" aria-hidden />
                      )}
                    </Link>
                    <div className="enrollment-card__body">
                      <Link to={`/courses/${e.courseId}`} className="enrollment-card__title">
                        {title}
                      </Link>
                      {course?.instructorName && (
                        <p className="enrollment-card__meta">{course.instructorName}</p>
                      )}
                      <time className="enrollment-card__date" dateTime={e.enrolledAt || undefined}>
                        Enrolled {e.enrolledAt ? new Date(e.enrolledAt).toLocaleDateString(undefined, { dateStyle: 'medium' }) : '—'}
                      </time>
                      <div className="enrollment-card__actions">
                        <Link to={`/courses/${e.courseId}`} className="btn enrollment-card__btn">
                          Continue learning
                        </Link>
                      </div>
                    </div>
                  </article>
                </li>
              )
            })}
          </ul>
        </>
      )}
    </div>
  )
}
