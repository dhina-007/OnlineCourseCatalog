import { getToken } from '@/lib/authStore.js'

/** API paths already include `/api/v1/...`; base must be origin only (no `/api/v1` suffix). */
function apiBaseUrl() {
  const raw = import.meta.env.VITE_API_URL
  const fallback = 'https://onlinecoursecatalog.onrender.com'
  if (raw == null || String(raw).trim() === '') {
    return fallback
  }
  return String(raw).trim().replace(/;+\s*$/, '').replace(/\/+$/, '') || fallback
}

const BASE = () => apiBaseUrl()

function buildHeaders(options, token) {
  const h = new Headers(options.headers)
  if (!(options.body instanceof FormData) && !h.has('Content-Type')) {
    h.set('Content-Type', 'application/json')
  }
  if (token) {
    h.set('Authorization', `Bearer ${token}`)
  }
  return h
}

export class ApiError extends Error {
  constructor(message, status, body) {
    super(message)
    this.status = status
    this.body = body
  }
}

export async function api(path, options = {}) {
  const token = getToken()
  const url = `${BASE()}${path.startsWith('/') ? path : `/${path}`}`
  const res = await fetch(url, {
    ...options,
    headers: buildHeaders(options, token),
  })

  if (res.status === 204) {
    return null
  }

  const ct = res.headers.get('content-type') || ''
  const isJson = ct.includes('application/json')
  const body = isJson ? await res.json().catch(() => null) : await res.text()

  if (!res.ok) {
    const msg =
      (body && typeof body === 'object' && (body.message || body.error)) ||
      (body && typeof body === 'object' && body.fields
        ? JSON.stringify(body.fields)
        : null) ||
      (typeof body === 'string' ? body : res.statusText)
    throw new ApiError(String(msg || 'Request failed'), res.status, body)
  }

  return body
}

export async function apiBlob(path, options = {}) {
  const token = getToken()
  const url = `${BASE()}${path.startsWith('/') ? path : `/${path}`}`
  const res = await fetch(url, {
    ...options,
    headers: buildHeaders(options, token),
  })
  if (!res.ok) {
    const t = await res.text()
    throw new ApiError(t || res.statusText, res.status, t)
  }
  return res.blob()
}
