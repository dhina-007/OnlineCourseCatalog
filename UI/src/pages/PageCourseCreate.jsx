import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { api, ApiError } from '@/lib/api.js'

function parseTags(s) {
  if (!s || !s.trim()) return []
  return s
    .split(',')
    .map((t) => t.trim())
    .filter(Boolean)
}

export function PageCourseCreate() {
  const nav = useNavigate()
  const [err, setErr] = useState('')
  const [ok, setOk] = useState('')
  const [mode, setMode] = useState('single')

  const [title, setTitle] = useState('')
  const [description, setDescription] = useState('')
  const [category, setCategory] = useState('')
  const [language, setLanguage] = useState('English')
  const [price, setPrice] = useState('0')
  const [currency, setCurrency] = useState('USD')
  const [free, setFree] = useState(true)
  const [videoUrl, setVideoUrl] = useState('')
  const [imageUrl, setImageUrl] = useState('')
  const [tags, setTags] = useState('')
  const [coverFile, setCoverFile] = useState(null)

  const [batchJson, setBatchJson] = useState('[]')

  async function submitSingle(e) {
    e.preventDefault()
    setErr('')
    setOk('')
    try {
      const body = {
        title,
        description: description || null,
        category,
        language,
        price: free ? 0 : Number(price),
        currency,
        free,
        videoUrl: videoUrl || null,
        imageUrl: imageUrl || null,
        tagNames: parseTags(tags),
      }
      const created = await api('/api/v1/courses', { method: 'POST', body: JSON.stringify(body) })
      if (coverFile) {
        const fd = new FormData()
        fd.append('file', coverFile)
        try {
          await api(`/api/v1/courses/${created.id}/cover`, { method: 'POST', body: fd })
        } catch (upEx) {
          setErr(
            (upEx instanceof ApiError ? upEx.message : 'Cover upload failed') +
              ' — course was created; you can upload cover from the course page.',
          )
          nav(`/courses/${created.id}`)
          return
        }
      }
      nav(`/courses/${created.id}`)
    } catch (ex) {
      setErr(ex instanceof ApiError ? ex.message : 'Create failed')
    }
  }

  async function submitBatch(e) {
    e.preventDefault()
    setErr('')
    setOk('')
    try {
      const arr = JSON.parse(batchJson)
      if (!Array.isArray(arr)) {
        setErr('JSON must be an array of course objects')
        return
      }
      const created = await api('/api/v1/courses/batch', { method: 'POST', body: JSON.stringify(arr) })
      setOk(`Created ${created.length} course(s). Use per-course page to upload covers if needed.`)
    } catch (ex) {
      if (ex instanceof SyntaxError) {
        setErr('Invalid JSON')
        return
      }
      setErr(ex instanceof ApiError ? ex.message : 'Batch create failed')
    }
  }

  return (
    <div className="card">
      <h1>Create courses</h1>
      <p className="muted">
        <Link to="/courses">← Courses</Link>
      </p>
      {err && <div className="err">{err}</div>}
      {ok && <div className="ok">{ok}</div>}

      <div className="row" style={{ marginBottom: '1rem' }}>
        <button type="button" className={mode === 'single' ? '' : 'secondary'} onClick={() => setMode('single')}>
          Single
        </button>
        <button type="button" className={mode === 'batch' ? '' : 'secondary'} onClick={() => setMode('batch')}>
          Batch (JSON array)
        </button>
      </div>

      {mode === 'single' ? (
        <form onSubmit={submitSingle}>
          <label>Title</label>
          <input value={title} onChange={(e) => setTitle(e.target.value)} required />
          <label>Description</label>
          <textarea value={description} onChange={(e) => setDescription(e.target.value)} />
          <label>Category</label>
          <input value={category} onChange={(e) => setCategory(e.target.value)} required />
          <label>Language</label>
          <input value={language} onChange={(e) => setLanguage(e.target.value)} required />
          <label>
            <input type="checkbox" checked={free} onChange={(e) => setFree(e.target.checked)} /> Free course
          </label>
          {!free && (
            <>
              <label>Price</label>
              <input value={price} onChange={(e) => setPrice(e.target.value)} type="number" step="0.01" min="0" />
              <label>Currency</label>
              <input value={currency} onChange={(e) => setCurrency(e.target.value)} />
            </>
          )}
          <label>Cover image URL (optional)</label>
          <input value={imageUrl} onChange={(e) => setImageUrl(e.target.value)} placeholder="Or upload file below" />
          <label>Upload cover to storage (optional, same S3 as video)</label>
          <input
            type="file"
            accept="image/*"
            onChange={(e) => setCoverFile(e.target.files?.[0] || null)}
          />
          <p className="muted" style={{ marginTop: 0 }}>
            If both URL and file are set, upload runs after create and overwrites with the stored public URL.
          </p>
          <label>Promo video URL (optional — plays on course page)</label>
          <input value={videoUrl} onChange={(e) => setVideoUrl(e.target.value)} />
          <label>Tags (comma-separated)</label>
          <input value={tags} onChange={(e) => setTags(e.target.value)} placeholder="Java, Spring" />
          <button type="submit">Create course</button>
        </form>
      ) : (
        <form onSubmit={submitBatch}>
          <label>Paste JSON array (max 100 courses). Include optional &quot;imageUrl&quot; per item.</label>
          <textarea value={batchJson} onChange={(e) => setBatchJson(e.target.value)} rows={16} />
          <button type="submit">Create batch</button>
        </form>
      )}
    </div>
  )
}
