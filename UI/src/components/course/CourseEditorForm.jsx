import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { api, ApiError } from '@/lib/api.js'

function parseTags(s) {
  if (!s || !s.trim()) return []
  return s
    .split(',')
    .map((t) => t.trim())
    .filter(Boolean)
}

export function CourseEditorForm({ course }) {
  const nav = useNavigate()
  const [err, setErr] = useState('')
  const [title, setTitle] = useState(course.title || '')
  const [description, setDescription] = useState(course.description || '')
  const [category, setCategory] = useState(course.category || '')
  const [language, setLanguage] = useState(course.language || '')
  const [price, setPrice] = useState(String(course.price ?? 0))
  const [currency, setCurrency] = useState(course.currency || 'USD')
  const [free, setFree] = useState(!!course.free)
  const [videoUrl, setVideoUrl] = useState(course.videoUrl || '')
  const [imageUrl, setImageUrl] = useState(course.imageUrl || '')
  const [tags, setTags] = useState((course.tags && [...course.tags].join(', ')) || '')

  useEffect(() => {
    setTitle(course.title || '')
    setDescription(course.description || '')
    setCategory(course.category || '')
    setLanguage(course.language || '')
    setPrice(String(course.price ?? 0))
    setCurrency(course.currency || 'USD')
    setFree(!!course.free)
    setVideoUrl(course.videoUrl || '')
    setImageUrl(course.imageUrl || '')
    setTags((course.tags && [...course.tags].join(', ')) || '')
  }, [course])

  async function save(e) {
    e.preventDefault()
    setErr('')
    try {
      await api(`/api/v1/courses/${course.id}`, {
        method: 'PUT',
        body: JSON.stringify({
          title,
          description,
          category,
          language,
          price: free ? 0 : Number(price),
          currency,
          free,
          videoUrl: videoUrl || null,
          imageUrl: imageUrl || null,
          tagNames: parseTags(tags),
        }),
      })
      window.location.reload()
    } catch (ex) {
      setErr(ex instanceof ApiError ? ex.message : 'Update failed')
    }
  }

  async function remove() {
    if (!confirm('Delete this course?')) return
    setErr('')
    try {
      await api(`/api/v1/courses/${course.id}`, { method: 'DELETE' })
      nav('/courses')
    } catch (ex) {
      setErr(ex instanceof ApiError ? ex.message : 'Delete failed')
    }
  }

  return (
    <div className="card">
      <h3>Edit course</h3>
      {err && <div className="err">{err}</div>}
      <form onSubmit={save}>
        <label>Title</label>
        <input value={title} onChange={(e) => setTitle(e.target.value)} required />
        <label>Description</label>
        <textarea value={description} onChange={(e) => setDescription(e.target.value)} />
        <label>Category</label>
        <input value={category} onChange={(e) => setCategory(e.target.value)} required />
        <label>Language</label>
        <input value={language} onChange={(e) => setLanguage(e.target.value)} required />
        <label>
          <input type="checkbox" checked={free} onChange={(e) => setFree(e.target.checked)} /> Free
        </label>
        {!free && (
          <>
            <label>Price</label>
            <input value={price} onChange={(e) => setPrice(e.target.value)} type="number" step="0.01" />
            <label>Currency</label>
            <input value={currency} onChange={(e) => setCurrency(e.target.value)} />
          </>
        )}
        <label>Cover image URL (optional — or use Upload cover below)</label>
        <input value={imageUrl} onChange={(e) => setImageUrl(e.target.value)} placeholder="https://…" />
        <label>Promo video URL (optional — YouTube or direct file URL)</label>
        <input value={videoUrl} onChange={(e) => setVideoUrl(e.target.value)} />
        <label>Tags (comma-separated)</label>
        <input value={tags} onChange={(e) => setTags(e.target.value)} />
        <button type="submit">Save changes</button>
        <button type="button" className="danger" onClick={remove}>
          Delete course
        </button>
      </form>
    </div>
  )
}
