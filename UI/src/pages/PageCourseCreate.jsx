import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { api, ApiError } from '@/lib/api.js'
import { ButtonSpinner } from '@/components/common/Loader.jsx'

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
  const [submitting, setSubmitting] = useState(false)

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
  const [promoVideoFile, setPromoVideoFile] = useState(null)

  const [batchJson, setBatchJson] = useState('[]')

  async function submitSingle(e) {
    e.preventDefault()
    setErr('')
    setOk('')
    setSubmitting(true)
    try {
      const body = {
        title,
        description: description || null,
        category,
        language,
        price: free ? 0 : Number(price),
        currency,
        free,
        videoUrl: promoVideoFile ? null : videoUrl || null,
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

      if (promoVideoFile) {
        const vfd = new FormData()
        vfd.append('file', promoVideoFile)
        try {
          await api(`/api/v1/courses/${created.id}/video`, { method: 'POST', body: vfd })
        } catch (upEx) {
          setErr(
            (upEx instanceof ApiError ? upEx.message : 'Promo video upload failed') +
              ' — course was created; upload video from the course page.',
          )
          nav(`/courses/${created.id}`)
          return
        }
      }

      nav(`/courses/${created.id}`)
    } catch (ex) {
      setErr(ex instanceof ApiError ? ex.message : 'Create failed')
    } finally {
      setSubmitting(false)
    }
  }

  async function submitBatch(e) {
    e.preventDefault()
    setErr('')
    setOk('')
    setSubmitting(true)
    try {
      const arr = JSON.parse(batchJson)
      if (!Array.isArray(arr)) {
        setErr('JSON must be an array of course objects')
        return
      }
      const created = await api('/api/v1/courses/batch', { method: 'POST', body: JSON.stringify(arr) })
      setOk(`Created ${created.length} course(s). Use per-course page to upload covers or promo video if needed.`)
    } catch (ex) {
      if (ex instanceof SyntaxError) {
        setErr('Invalid JSON')
        return
      }
      setErr(ex instanceof ApiError ? ex.message : 'Batch create failed')
    } finally {
      setSubmitting(false)
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
        <button
          type="button"
          className={mode === 'single' ? '' : 'secondary'}
          disabled={submitting}
          onClick={() => setMode('single')}
        >
          Single
        </button>
        <button
          type="button"
          className={mode === 'batch' ? '' : 'secondary'}
          disabled={submitting}
          onClick={() => setMode('batch')}
        >
          Batch (JSON array)
        </button>
      </div>

      {mode === 'single' ? (
        <form onSubmit={submitSingle}>
          <label>Title</label>
          <input value={title} onChange={(e) => setTitle(e.target.value)} required disabled={submitting} />
          <label>Description</label>
          <textarea value={description} onChange={(e) => setDescription(e.target.value)} disabled={submitting} />
          <label>Category</label>
          <input value={category} onChange={(e) => setCategory(e.target.value)} required disabled={submitting} />
          <label>Language</label>
          <input value={language} onChange={(e) => setLanguage(e.target.value)} required disabled={submitting} />
          <label>
            <input type="checkbox" checked={free} onChange={(e) => setFree(e.target.checked)} disabled={submitting} />{' '}
            Free course
          </label>
          {!free && (
            <>
              <label>Price</label>
              <input
                value={price}
                onChange={(e) => setPrice(e.target.value)}
                type="number"
                step="0.01"
                min="0"
                disabled={submitting}
              />
              <label>Currency</label>
              <input value={currency} onChange={(e) => setCurrency(e.target.value)} disabled={submitting} />
            </>
          )}
          <label>Cover image URL (optional)</label>
          <input
            value={imageUrl}
            onChange={(e) => setImageUrl(e.target.value)}
            placeholder="Or upload file below"
            disabled={submitting}
          />
          <label>Upload cover to storage (optional, same S3 as video)</label>
          <input
            type="file"
            accept="image/*"
            disabled={submitting}
            onChange={(e) => setCoverFile(e.target.files?.[0] || null)}
          />
          <p className="muted" style={{ marginTop: 0 }}>
            If both URL and file are set, upload runs after create and overwrites with the stored public URL.
          </p>
          <label>Promo video URL (optional — YouTube or direct URL)</label>
          <input
            value={videoUrl}
            onChange={(e) => setVideoUrl(e.target.value)}
            placeholder="Ignored if you upload a file below"
            disabled={submitting || !!promoVideoFile}
          />
          <label>Upload promo video (optional — same S3 bucket as course page upload)</label>
          <input
            type="file"
            accept="video/*"
            disabled={submitting}
            onChange={(e) => setPromoVideoFile(e.target.files?.[0] || null)}
          />
          <p className="muted" style={{ marginTop: 0 }}>
            File upload runs after the course is created and sets the course promo video URL. If you pick a file, the
            promo URL field above is skipped for the initial create.
          </p>
          <label>Tags (comma-separated)</label>
          <input
            value={tags}
            onChange={(e) => setTags(e.target.value)}
            placeholder="Java, Spring"
            disabled={submitting}
          />
          <button type="submit" disabled={submitting}>
            <ButtonSpinner busy={submitting} busyLabel="Creating…">
              Create course
            </ButtonSpinner>
          </button>
        </form>
      ) : (
        <form onSubmit={submitBatch}>
          <label>Paste JSON array (max 100 courses). Include optional &quot;imageUrl&quot; per item.</label>
          <textarea value={batchJson} onChange={(e) => setBatchJson(e.target.value)} rows={16} disabled={submitting} />
          <button type="submit" disabled={submitting}>
            <ButtonSpinner busy={submitting} busyLabel="Creating…">
              Create batch
            </ButtonSpinner>
          </button>
        </form>
      )}
    </div>
  )
}
