/** Matches backend @PreAuthorize for instructor/admin mutations */
export function canEdit(role) {
  return role === 'ADMIN' || role === 'INSTRUCTOR'
}

export function isAdmin(role) {
  return role === 'ADMIN'
}
