import { Navigate, Outlet } from 'react-router-dom'
import { useAuth } from '@/lib/authStore.js'
import { canEdit } from '@/lib/roles.js'

export function GuardInstructor() {
  const auth = useAuth()
  if (!auth?.token) {
    return <Navigate to="/login" replace />
  }
  if (!canEdit(auth.role)) {
    return <Navigate to="/" replace />
  }
  return <Outlet />
}
