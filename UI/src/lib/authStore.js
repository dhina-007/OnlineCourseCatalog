import { useSyncExternalStore } from 'react'

const STORAGE_KEY = 'auth'

let cache = loadFromStorage()
const listeners = new Set()

function loadFromStorage() {
  try {
    const raw = localStorage.getItem(STORAGE_KEY)
    if (!raw) return null
    const o = JSON.parse(raw)
    if (!o?.token) return null
    return o
  } catch {
    return null
  }
}

function persist(session) {
  cache = session
  if (session) {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(session))
  } else {
    localStorage.removeItem(STORAGE_KEY)
  }
  listeners.forEach((fn) => fn())
}

export function subscribeAuth(listener) {
  listeners.add(listener)
  return () => listeners.delete(listener)
}

export function getAuthSnapshot() {
  return cache
}

export function getToken() {
  return cache?.token ?? null
}

export function setAuthSession(session) {
  persist({
    token: session.token,
    email: session.email,
    userId: session.userId,
    role: session.role,
  })
}

export function clearAuthSession() {
  persist(null)
}

export function useAuth() {
  return useSyncExternalStore(subscribeAuth, getAuthSnapshot, () => null)
}
