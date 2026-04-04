/**
 * Consistent loading UI: inline spinner, full-page block, and button-friendly spinner.
 */
export function Spinner({ size = 'md', className = '' }) {
  return (
    <span
      className={`spinner spinner--${size} ${className}`.trim()}
      role="status"
      aria-live="polite"
      aria-label="Loading"
    />
  )
}

export function PageLoader({ message = 'Loading…' }) {
  return (
    <div className="page-loader" role="status" aria-live="polite">
      <Spinner size="lg" />
      <p className="page-loader__text">{message}</p>
    </div>
  )
}

export function InlineLoader({ message = 'Loading…' }) {
  return (
    <span className="inline-loader">
      <Spinner size="sm" />
      <span className="inline-loader__msg">{message}</span>
    </span>
  )
}

/** Wrap primary button content when submitting */
export function ButtonSpinner({ busy, children, busyLabel = 'Please wait…' }) {
  if (busy) {
    return (
      <>
        <Spinner size="sm" className="spinner--on-dark" />
        <span className="btn-spinner-gap">{busyLabel}</span>
      </>
    )
  }
  return children
}
