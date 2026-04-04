import { Navigate, Outlet } from 'react-router-dom'
import { useAuth } from '@/lib/authStore.js'

export function PrivateRoute() {
  const auth = useAuth()
  if (!auth?.token) {
    return <Navigate to="/login" replace />
  }
  return <Outlet />
}
