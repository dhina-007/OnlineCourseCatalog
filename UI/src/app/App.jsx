import { Navigate, Route, Routes } from 'react-router-dom'
import { GuardInstructor } from '@/components/auth/GuardInstructor.jsx'
import { Layout } from '@/components/layout/Layout.jsx'
import { PrivateRoute } from '@/components/auth/PrivateRoute.jsx'
import { PageAdminUsers } from '@/pages/PageAdminUsers.jsx'
import { PageCatalog } from '@/pages/PageCatalog.jsx'
import { PageCourseCreate } from '@/pages/PageCourseCreate.jsx'
import { PageCourseDetail } from '@/pages/PageCourseDetail.jsx'
import { PageEnrollments } from '@/pages/PageEnrollments.jsx'
import { PageLanding } from '@/pages/PageLanding.jsx'
import { PageLogin } from '@/pages/PageLogin.jsx'
import { PageProfile } from '@/pages/PageProfile.jsx'
import { PageRegister } from '@/pages/PageRegister.jsx'

export function App() {
  return (
    <Routes>
      <Route path="/login" element={<PageLogin />} />
      <Route path="/register" element={<PageRegister />} />
      <Route element={<Layout />}>
        <Route path="/" element={<PageLanding />} />
        <Route path="/courses" element={<PageCatalog />} />
        <Route path="/courses/:id" element={<PageCourseDetail />} />
        <Route element={<PrivateRoute />}>
          <Route element={<GuardInstructor />}>
            <Route path="/courses/new" element={<PageCourseCreate />} />
          </Route>
          <Route path="/me/enrollments" element={<PageEnrollments />} />
          <Route path="/me" element={<PageProfile />} />
          <Route path="/admin/users" element={<PageAdminUsers />} />
        </Route>
      </Route>
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  )
}
