/**
 * Plays promo / lesson video: YouTube embed or native <video> for direct URLs (e.g. S3 public URL).
 */
export function youtubeVideoId(url) {
  if (!url || typeof url !== 'string') return null
  try {
    const u = new URL(url.trim())
    const host = u.hostname.replace(/^www\./, '')
    if (host === 'youtu.be') {
      const id = u.pathname.replace(/^\//, '').split('/')[0]
      return id || null
    }
    if (host.includes('youtube.com')) {
      const v = u.searchParams.get('v')
      if (v) return v
      const parts = u.pathname.split('/').filter(Boolean)
      const i = parts.indexOf('embed')
      if (i >= 0 && parts[i + 1]) return parts[i + 1]
      const si = parts.indexOf('shorts')
      if (si >= 0 && parts[si + 1]) return parts[si + 1]
    }
  } catch {
    return null
  }
  return null
}

/** Public YouTube watch URL for optional external preview link when inline player is gated. */
export function youtubeWatchPageUrl(url) {
  const id = youtubeVideoId(url)
  return id ? `https://www.youtube.com/watch?v=${id}` : null
}

export function CourseVideoPlayer({ url, title = 'Course preview' }) {
  if (!url) return null
  const yt = youtubeVideoId(url)
  if (yt) {
    return (
      <div className="video-shell">
        <iframe
          title={title}
          src={`https://www.youtube-nocookie.com/embed/${yt}`}
          allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
          allowFullScreen
          className="video-iframe"
        />
      </div>
    )
  }
  return (
    <div className="video-shell">
      <video className="video-native" controls playsInline preload="metadata" src={url}>
        Your browser does not support embedded video.
      </video>
    </div>
  )
}
