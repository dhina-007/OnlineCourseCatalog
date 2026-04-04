import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { api, ApiError } from '@/lib/api.js'
import { useAuth } from '@/lib/authStore.js'
import { canEdit } from '@/lib/roles.js'
import { PageLoader, Spinner } from '@/components/common/Loader.jsx'

export function PageCatalog() {
  const auth = useAuth()
  const [page, setPage] = useState(0)
  const [size] = useState(12)
  const [data, setData] = useState(null)
  const [err, setErr] = useState('')
  const [search, setSearch] = useState('')
  const [q, setQ] = useState('')
  const [loading, setLoading] = useState(true)

  const SEARCH_DEBOUNCE_MS = 350

  /** Typing updates the input; debounced query drives the API. Clearing the field resets immediately (full catalog). */
  useEffect(() => {
    const trimmed = search.trim()
    if (trimmed === '') {
      setQ('')
      setPage(0)
      return
    }
    const t = setTimeout(() => {
      setQ(trimmed)
      setPage(0)
    }, SEARCH_DEBOUNCE_MS)
    return () => clearTimeout(t)
  }, [search])

  useEffect(() => {
    let cancelled = false
    ;(async () => {
      setLoading(true)
      setErr('')
      try {
        const params = new URLSearchParams({ page: String(page), size: String(size) })
        if (q.trim()) params.set('search', q.trim())
        const res = await api(`/api/v1/courses?${params}`)
        if (!cancelled) setData(res)
      } catch (e) {
        if (!cancelled) setErr(e instanceof ApiError ? e.message : 'Failed to load')
      } finally {
        if (!cancelled) setLoading(false)
      }
    })()
    return () => {
      cancelled = true
    }
  }, [page, size, q])

  /** Submit runs search immediately (no need to wait for debounce). */
  function doSearch(e) {
    e.preventDefault()
    const trimmed = search.trim()
    setQ(trimmed)
    setPage(0)
  }

  if (loading && !data) {
    return <PageLoader message="Loading courses…" />
  }

  return (
    <>
      <div
        className="row catalog-toolbar"
        style={{ justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '1rem' }}
      >
        <div className="catalog-hero">
          <h1>Courses to get you started</h1>
          <p>Learn from real-world experts — browse, search, and enroll.</p>
        </div>
        {auth?.token && canEdit(auth.role) && (
          <Link to="/courses/new" className="btn">
            Create course
          </Link>
        )}
      </div>

      <form onSubmit={doSearch} className="search-strip">
        <input
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          placeholder="Search for anything"
          aria-label="Search courses"
          autoComplete="off"
          disabled={loading}
        />
        <button type="submit" disabled={loading}>
          Search
        </button>
      </form>

      {loading && data && (
        <div className="catalog-loading-bar" role="status">
          <Spinner size="sm" />
          <span>Updating results…</span>
        </div>
      )}

      {err && <div className="err">{err}</div>}

      <div className="course-grid">
        {(data?.content || []).map((c) => {
          const bestseller = c.averageRating != null && c.averageRating >= 4.5
          return (
            <Link key={c.id} to={`/courses/${c.id}`} className="udemy-course-card">
              <div className="udemy-course-thumb">
                {c.imageUrl ? (
                  <img src={c.imageUrl} alt="" loading="lazy" decoding="async" />
                ) : (
                  <div className="udemy-course-thumb-fallback" aria-hidden />
                )}
                <div className="udemy-course-badges">
                  {!c.free && <span className="badge badge--premium">Premium</span>}
                  {bestseller && <span className="badge badge--bestseller">Bestseller</span>}
                </div>
              </div>
              <div className="udemy-course-body">
                <h3>{c.title}</h3>
                <p className="udemy-course-instructor">{c.instructorName}</p>
                {c.averageRating != null && (
                  <p className="udemy-course-rating">
                    <span className="star">★</span> {c.averageRating.toFixed(1)}
                  </p>
                )}
                <p className="udemy-course-price">
                  {c.free ? 'Free' : `${c.currency} ${Number(c.price).toFixed(0)}`}
                </p>
              </div>
            </Link>
          )
        })}
      </div>

      {data && (
        <div className="pager">
          <button type="button" className="secondary" disabled={data.first || loading} onClick={() => setPage((p) => p - 1)}>
            Previous
          </button>
          <span className="muted">
            Page {data.number + 1} of {data.totalPages || 1}
          </span>
          <button type="button" className="secondary" disabled={data.last || loading} onClick={() => setPage((p) => p + 1)}>
            Next
          </button>
        </div>
      )}
    </>
  )
}
