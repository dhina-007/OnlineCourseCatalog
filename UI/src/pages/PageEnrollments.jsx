import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { api, ApiError } from '@/lib/api.js'

export function PageEnrollments() {
  const [list, setList] = useState([])
  const [err, setErr] = useState('')

  useEffect(() => {
    let c = false
    ;(async () => {
      try {
        const res = await api('/api/v1/users/me/enrollments')
        if (!c) setList(res || [])
      } catch (e) {
        if (!c) setErr(e instanceof ApiError ? e.message : 'Failed to load')
      }
    })()
    return () => {
      c = true
    }
  }, [])

  return (
    <div className="card">
      <h1>My enrollments</h1>
      {err && <div className="err">{err}</div>}
      {list.length === 0 && !err && <p className="muted">No enrollments yet.</p>}
      <ul style={{ paddingLeft: '1.2rem' }}>
        {list.map((e) => (
          <li key={e.id} style={{ marginBottom: '0.5rem' }}>
            <Link to={`/courses/${e.courseId}`}>Course #{e.courseId}</Link>
            <span className="muted" style={{ marginLeft: '0.5rem' }}>
              {e.enrolledAt && new Date(e.enrolledAt).toLocaleString()}
            </span>
          </li>
        ))}
      </ul>
    </div>
  )
}
