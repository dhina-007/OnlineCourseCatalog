import { useCallback, useEffect, useMemo, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import { api, ApiError } from '@/lib/api.js'
import { useAuth } from '@/lib/authStore.js'
import { CheckoutStripe } from '@/components/checkout/CheckoutStripe.jsx'
import { CourseEditorForm } from '@/components/course/CourseEditorForm.jsx'
import { CourseVideoPlayer, youtubeWatchPageUrl } from '@/components/course/CourseVideoPlayer.jsx'
import { canEdit } from '@/lib/roles.js'

export function PageCourseDetail() {
  const { id } = useParams()
  const courseId = Number(id)
  const invalidId = !Number.isFinite(courseId) || courseId <= 0
  const auth = useAuth()

  const [course, setCourse] = useState(null)
  const [lessons, setLessons] = useState([])
  const [reviews, setReviews] = useState([])
  const [enrolled, setEnrolled] = useState(false)
  const [progress, setProgress] = useState([])
  const [students, setStudents] = useState([])
  const [expandedLesson, setExpandedLesson] = useState(null)
  const [materials, setMaterials] = useState({})

  const [err, setErr] = useState('')
  const [msg, setMsg] = useState('')
  const [checkout, setCheckout] = useState(null)

  const [reviewRating, setReviewRating] = useState(5)
  const [reviewComment, setReviewComment] = useState('')

  const [lessonTitle, setLessonTitle] = useState('')
  const [lessonContent, setLessonContent] = useState('')
  const [matType, setMatType] = useState('PDF')
  const [matUrl, setMatUrl] = useState('')
  const [matLessonId, setMatLessonId] = useState(null)

  const editable = useMemo(() => auth?.token && canEdit(auth.role), [auth?.token, auth?.role])

  const loadPublic = useCallback(async () => {
    if (invalidId) return
    const [c, l, r] = await Promise.all([
      api(`/api/v1/courses/${courseId}`),
      api(`/api/v1/courses/${courseId}/lessons`),
      api(`/api/v1/courses/${courseId}/reviews`),
    ])
    setCourse(c)
    setLessons(l || [])
    setReviews(r || [])
  }, [courseId, invalidId])

  const loadAuthData = useCallback(async () => {
    if (invalidId) {
      setEnrolled(false)
      setProgress([])
      setStudents([])
      return
    }
    if (!auth?.token) {
      setEnrolled(false)
      setProgress([])
      setStudents([])
      return
    }
    try {
      const enrolls = await api('/api/v1/users/me/enrollments')
      setEnrolled((enrolls || []).some((e) => e.courseId === courseId))
    } catch {
      setEnrolled(false)
    }
    try {
      const prog = await api(`/api/v1/courses/${courseId}/progress`)
      setProgress(prog || [])
    } catch {
      setProgress([])
    }
    if (editable) {
      try {
        const studs = await api(`/api/v1/courses/${courseId}/students`)
        setStudents(studs || [])
      } catch {
        setStudents([])
      }
    } else {
      setStudents([])
    }
  }, [auth?.token, courseId, editable, invalidId])

  useEffect(() => {
    let c = false
    ;(async () => {
      setErr('')
      try {
        await loadPublic()
      } catch (e) {
        if (!c) setErr(e instanceof ApiError ? e.message : 'Failed to load course')
      }
    })()
    return () => {
      c = true
    }
  }, [loadPublic])

  useEffect(() => {
    loadAuthData()
  }, [loadAuthData])

  const showPromoVideo = Boolean(course?.videoUrl && (course.free || enrolled || editable))
  const canWatchLessonMedia = Boolean(course?.free || enrolled || editable)
  const externalPreviewUrl = course?.videoUrl ? youtubeWatchPageUrl(course.videoUrl) : null

  async function refreshAll() {
    setErr('')
    try {
      await loadPublic()
      await loadAuthData()
    } catch (e) {
      setErr(e instanceof ApiError ? e.message : 'Refresh failed')
    }
  }

  async function loadMaterialsForLesson(lessonId) {
    if (!auth?.token) return
    try {
      const m = await api(`/api/v1/courses/${courseId}/lessons/${lessonId}/materials`)
      setMaterials((prev) => ({ ...prev, [lessonId]: m || [] }))
    } catch (e) {
      setErr(e instanceof ApiError ? e.message : 'Materials failed')
    }
  }

  function toggleLesson(lid) {
    if (expandedLesson === lid) {
      setExpandedLesson(null)
    } else {
      setExpandedLesson(lid)
      if (!materials[lid]) {
        loadMaterialsForLesson(lid)
      }
    }
  }

  async function enroll() {
    setMsg('')
    setErr('')
    try {
      await api(`/api/v1/courses/${courseId}/enroll`, { method: 'POST' })
      setMsg('Enrolled.')
      await loadAuthData()
    } catch (e) {
      setErr(e instanceof ApiError ? e.message : 'Enroll failed')
    }
  }

  async function doCheckout() {
    setMsg('')
    setErr('')
    setCheckout(null)
    try {
      const co = await api('/api/v1/orders/checkout', {
        method: 'POST',
        body: JSON.stringify({ courseId }),
      })
      if (co.status === 'MOCK_PAID' || co.status === 'ALREADY_PAID') {
        setMsg(co.status === 'MOCK_PAID' ? 'Mock payment complete. You can enroll now.' : 'Already purchased.')
        await enroll()
        return
      }
      if (co.status === 'REQUIRES_PAYMENT' && co.clientSecret) {
        setCheckout({ clientSecret: co.clientSecret })
        return
      }
      setMsg(co.status || 'Checkout done')
    } catch (e) {
      setErr(e instanceof ApiError ? e.message : 'Checkout failed')
    }
  }

  async function submitReview(e) {
    e.preventDefault()
    setErr('')
    try {
      await api(`/api/v1/courses/${courseId}/reviews`, {
        method: 'POST',
        body: JSON.stringify({ rating: Number(reviewRating), comment: reviewComment || null }),
      })
      setReviewComment('')
      await loadPublic()
      setMsg('Review posted.')
    } catch (ex) {
      setErr(ex instanceof ApiError ? ex.message : 'Review failed')
    }
  }

  async function addLesson(e) {
    e.preventDefault()
    setErr('')
    try {
      await api(`/api/v1/courses/${courseId}/lessons`, {
        method: 'POST',
        body: JSON.stringify({
          title: lessonTitle,
          content: lessonContent || null,
          duration: null,
          videoUrl: null,
        }),
      })
      setLessonTitle('')
      setLessonContent('')
      await loadPublic()
    } catch (ex) {
      setErr(ex instanceof ApiError ? ex.message : 'Lesson create failed')
    }
  }

  async function deleteLesson(lid) {
    if (!confirm('Delete lesson?')) return
    setErr('')
    try {
      await api(`/api/v1/courses/${courseId}/lessons/${lid}`, { method: 'DELETE' })
      await loadPublic()
    } catch (ex) {
      setErr(ex instanceof ApiError ? ex.message : 'Delete failed')
    }
  }

  async function addMaterial(e) {
    e.preventDefault()
    if (!matLessonId) {
      setErr('Pick a lesson for the material')
      return
    }
    setErr('')
    try {
      await api(`/api/v1/courses/${courseId}/lessons/${matLessonId}/materials`, {
        method: 'POST',
        body: JSON.stringify({ type: matType, url: matUrl }),
      })
      setMatUrl('')
      await loadMaterialsForLesson(matLessonId)
    } catch (ex) {
      setErr(ex instanceof ApiError ? ex.message : 'Material add failed')
    }
  }

  async function saveProgress(lessonId, body) {
    setErr('')
    try {
      await api(`/api/v1/courses/${courseId}/lessons/${lessonId}/progress`, {
        method: 'PUT',
        body: JSON.stringify(body),
      })
      const prog = await api(`/api/v1/courses/${courseId}/progress`)
      setProgress(prog || [])
      setMsg('Progress saved.')
    } catch (ex) {
      setErr(ex instanceof ApiError ? ex.message : 'Progress save failed')
    }
  }

  async function uploadVideo(e) {
    e.preventDefault()
    const file = e.target.elements.file?.files?.[0]
    if (!file) return
    setErr('')
    const fd = new FormData()
    fd.append('file', file)
    try {
      await api(`/api/v1/courses/${courseId}/video`, { method: 'POST', body: fd })
      await loadPublic()
      setMsg('Video uploaded.')
    } catch (ex) {
      setErr(ex instanceof ApiError ? ex.message : 'Upload failed')
    }
  }

  async function uploadCover(e) {
    e.preventDefault()
    const file = e.target.elements.coverfile?.files?.[0]
    if (!file) return
    setErr('')
    const fd = new FormData()
    fd.append('file', file)
    try {
      await api(`/api/v1/courses/${courseId}/cover`, { method: 'POST', body: fd })
      await loadPublic()
      setMsg('Cover image uploaded.')
    } catch (ex) {
      setErr(ex instanceof ApiError ? ex.message : 'Cover upload failed')
    }
  }

  function progressFor(lessonId) {
    return progress.find((p) => p.lessonId === lessonId)
  }

  if (invalidId) {
    return (
      <div className="card err">
        Invalid course id. <Link to="/courses">Courses</Link>
      </div>
    )
  }

  if (err && !course) {
    return (
      <div className="card">
        <div className="err">{err}</div>
        <Link to="/courses">← Courses</Link>
      </div>
    )
  }

  if (!course) {
    return <p className="muted">Loading…</p>
  }

  return (
    <>
      <p className="muted">
        <Link to="/courses">← Courses</Link>
      </p>
      {err && <div className="err">{err}</div>}
      {msg && <div className="ok">{msg}</div>}

      <div className="course-detail-hero">
        <div className="course-detail-cover">
          {course.imageUrl ? (
            <img src={course.imageUrl} alt="" />
          ) : (
            <div className="course-detail-cover-fallback" aria-hidden />
          )}
        </div>
        <div className="course-detail-intro">
          <h1>{course.title}</h1>
          <p className="muted" style={{ marginBottom: '0.75rem' }}>
            {course.category} · {course.instructorName} · {course.language}
          </p>
          <p style={{ fontWeight: 700, margin: '0.5rem 0' }}>
            {course.free ? (
              'Free course'
            ) : (
              <>
                {course.currency} {Number(course.price).toFixed(2)}
              </>
            )}
            {course.averageRating != null && (
              <span className="udemy-course-rating" style={{ marginLeft: '0.75rem' }}>
                <span className="star">★</span> {course.averageRating.toFixed(1)}
              </span>
            )}
          </p>
        </div>
      </div>

      {course.videoUrl && showPromoVideo && (
        <div className="card">
          <h2 style={{ marginTop: 0 }}>Preview this course</h2>
          <CourseVideoPlayer url={course.videoUrl} title={course.title} />
        </div>
      )}

      {course.videoUrl && !showPromoVideo && (
        <div className="card preview-locked-card">
          <h2 style={{ marginTop: 0 }}>Course preview</h2>
          <p className="muted" style={{ marginTop: 0 }}>
            Purchase and enroll to unlock the full inline preview and lesson videos for this course.
          </p>
          {externalPreviewUrl && (
            <p style={{ marginBottom: 0 }}>
              <a href={externalPreviewUrl} target="_blank" rel="noreferrer">
                Open public sample on YouTube (new tab)
              </a>
            </p>
          )}
        </div>
      )}

      <div className="card">
        <h2 style={{ marginTop: 0 }}>About this course</h2>
        <p style={{ whiteSpace: 'pre-wrap' }}>{course.description}</p>
        <div style={{ marginTop: '0.75rem' }}>
          {(course.tags || []).map((t) => (
            <span key={t} className="tag">
              {t}
            </span>
          ))}
        </div>
      </div>

      {auth?.token && !editable && (
        <div className="card">
          <h3>Student</h3>
          {!course.free && !enrolled && (
            <>
              <p className="muted">Purchase required before enrolling.</p>
              <button type="button" onClick={doCheckout}>
                Checkout
              </button>
              {checkout?.clientSecret && (
                <div style={{ marginTop: '1rem' }}>
                  <CheckoutStripe
                    clientSecret={checkout.clientSecret}
                    onSuccess={async () => {
                      setCheckout(null)
                      setMsg('Payment successful. Enrolling…')
                      await enroll()
                    }}
                  />
                </div>
              )}
            </>
          )}
          {course.free && !enrolled && (
            <button type="button" onClick={enroll}>
              Enroll (free)
            </button>
          )}
          {enrolled && <p className="ok">You are enrolled.</p>}
        </div>
      )}

      {auth?.token && !editable && enrolled && (
        <div className="card">
          <h3>Your review</h3>
          <form onSubmit={submitReview} className="row">
            <div style={{ flex: 1 }}>
              <label>Rating (1–5)</label>
              <input
                type="number"
                min={1}
                max={5}
                value={reviewRating}
                onChange={(e) => setReviewRating(e.target.value)}
              />
            </div>
            <div style={{ flex: 3 }}>
              <label>Comment</label>
              <textarea value={reviewComment} onChange={(e) => setReviewComment(e.target.value)} rows={2} />
            </div>
            <button type="submit">Post review</button>
          </form>
        </div>
      )}

      {editable && (
        <>
          <CourseEditorForm course={course} />
          <div className="card">
            <h3>Course cover image</h3>
            <p className="muted">Same Supabase S3 bucket as video. Public URL is stored on the course.</p>
            <form onSubmit={uploadCover}>
              <input type="file" name="coverfile" accept="image/*" />
              <button type="submit">Upload cover</button>
            </form>
          </div>
          <div className="card">
            <h3>Promo video upload</h3>
            <form onSubmit={uploadVideo}>
              <input type="file" name="file" accept="video/*" />
              <button type="submit">Upload video</button>
            </form>
          </div>
          {students.length > 0 && (
            <div className="card">
              <h3>Enrolled students</h3>
              <table className="simple">
                <thead>
                  <tr>
                    <th>Name</th>
                    <th>Email</th>
                  </tr>
                </thead>
                <tbody>
                  {students.map((s) => (
                    <tr key={s.id}>
                      <td>{s.name}</td>
                      <td>{s.email}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
          <div className="card">
            <h3>Add lesson</h3>
            <form onSubmit={addLesson}>
              <label>Title</label>
              <input value={lessonTitle} onChange={(e) => setLessonTitle(e.target.value)} required />
              <label>Content</label>
              <textarea value={lessonContent} onChange={(e) => setLessonContent(e.target.value)} />
              <button type="submit">Add lesson</button>
            </form>
          </div>
          <div className="card">
            <h3>Add material to lesson</h3>
            <form onSubmit={addMaterial}>
              <label>Lesson</label>
              <select value={matLessonId || ''} onChange={(e) => setMatLessonId(Number(e.target.value))}>
                <option value="">Select…</option>
                {lessons.map((l) => (
                  <option key={l.id} value={l.id}>
                    {l.title}
                  </option>
                ))}
              </select>
              <label>Type</label>
              <select value={matType} onChange={(e) => setMatType(e.target.value)}>
                <option value="PDF">PDF</option>
                <option value="VIDEO">VIDEO</option>
              </select>
              <label>URL</label>
              <input value={matUrl} onChange={(e) => setMatUrl(e.target.value)} required />
              <button type="submit">Add material</button>
            </form>
          </div>
        </>
      )}

      <div className="card">
        <h2>Lessons</h2>
        {lessons.map((lesson) => (
          <div key={lesson.id} className="lesson-block">
            <div className="row" style={{ justifyContent: 'space-between' }}>
              <strong>{lesson.title}</strong>
              <div>
                <button type="button" className="secondary" onClick={() => toggleLesson(lesson.id)}>
                  {expandedLesson === lesson.id ? 'Hide' : 'Materials'}
                </button>
                {editable && (
                  <button type="button" className="danger" onClick={() => deleteLesson(lesson.id)}>
                    Delete
                  </button>
                )}
              </div>
            </div>
            {lesson.content && <p style={{ fontSize: '0.95rem' }}>{lesson.content}</p>}
            {lesson.videoUrl && canWatchLessonMedia && (
              <div style={{ marginTop: '0.75rem' }}>
                <CourseVideoPlayer url={lesson.videoUrl} title={lesson.title} />
              </div>
            )}
            {lesson.videoUrl && !canWatchLessonMedia && (
              <p className="muted" style={{ marginTop: '0.75rem' }}>
                Lesson video unlocks after you enroll in this course.
              </p>
            )}
            {expandedLesson === lesson.id && (
              <div style={{ marginTop: '0.5rem' }}>
                {!auth?.token && <p className="muted">Log in to view materials.</p>}
                {auth?.token && (
                  <>
                    <p className="muted">Materials</p>
                    <ul>
                      {(materials[lesson.id] || []).map((m) => (
                        <li key={m.id}>
                          <a href={m.url} target="_blank" rel="noreferrer">
                            {m.type}
                          </a>
                        </li>
                      ))}
                    </ul>
                    {(!materials[lesson.id] || materials[lesson.id].length === 0) && (
                      <p className="muted">No materials (or still loading).</p>
                    )}
                  </>
                )}
              </div>
            )}
            {auth?.token && enrolled && !editable && (
              <ProgressMini
                lessonId={lesson.id}
                initial={progressFor(lesson.id)}
                onSave={(body) => saveProgress(lesson.id, body)}
              />
            )}
          </div>
        ))}
        {lessons.length === 0 && <p className="muted">No lessons yet.</p>}
      </div>

      <div className="card">
        <h2>Reviews</h2>
        {(reviews || []).map((rv) => (
          <div key={rv.id} style={{ borderBottom: '1px solid var(--border)', padding: '0.5rem 0' }}>
            <strong>{rv.userName}</strong>{' '}
            <span className="muted">
              ★ {rv.rating} · {rv.createdAt && new Date(rv.createdAt).toLocaleDateString()}
            </span>
            {rv.comment && <p style={{ margin: '0.25rem 0 0' }}>{rv.comment}</p>}
          </div>
        ))}
        {reviews.length === 0 && <p className="muted">No reviews yet.</p>}
      </div>

      <button type="button" className="secondary" onClick={refreshAll}>
        Refresh
      </button>
    </>
  )
}

function ProgressMini({ lessonId, initial, onSave }) {
  const [completed, setCompleted] = useState(initial?.completed ?? false)
  const [pct, setPct] = useState(initial?.progressPercent ?? 0)
  const [pos, setPos] = useState(initial?.lastPositionSec ?? 0)

  useEffect(() => {
    setCompleted(initial?.completed ?? false)
    setPct(initial?.progressPercent ?? 0)
    setPos(initial?.lastPositionSec ?? 0)
  }, [lessonId, initial])

  return (
    <div style={{ marginTop: '0.5rem', padding: '0.5rem', background: 'var(--bg)', borderRadius: 6 }}>
      <span className="muted">Progress</span>
      <label>
        <input type="checkbox" checked={completed} onChange={(e) => setCompleted(e.target.checked)} /> Completed
      </label>
      <label>% </label>
      <input
        type="number"
        min={0}
        max={100}
        value={pct}
        onChange={(e) => setPct(Number(e.target.value))}
        style={{ width: 80, display: 'inline-block' }}
      />
      <label> Position (sec)</label>
      <input
        type="number"
        min={0}
        value={pos}
        onChange={(e) => setPos(Number(e.target.value))}
        style={{ width: 80, display: 'inline-block' }}
      />
      <button
        type="button"
        className="secondary"
        onClick={() =>
          onSave({
            completed,
            progressPercent: pct,
            lastPositionSec: pos,
          })
        }
      >
        Save progress
      </button>
    </div>
  )
}
