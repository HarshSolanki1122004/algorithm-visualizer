/**
 * Complexity card for the selected algorithm. Every value comes from the
 * backend's GET /algorithms response — the frontend never hardcodes Big-O data.
 * If the backend is unreachable (fallback list, no metadata), it degrades to
 * just the name.
 */
export default function BigOCard({ info }) {
  if (!info) return null

  const hasComplexity = info.bestCase != null
  const rows = [
    ['Best', info.bestCase],
    ['Average', info.averageCase],
    ['Worst', info.worstCase],
    ['Space', info.spaceComplexity],
  ]

  return (
    <div className="rounded-box border border-edge bg-surface-base p-3">
      <p className="font-mono text-sm font-semibold text-ink">{info.displayName}</p>
      {info.description && (
        <p className="mt-1 text-xs leading-relaxed text-ink-faint">{info.description}</p>
      )}

      {hasComplexity ? (
        <>
          <dl className="mt-3 grid grid-cols-2 gap-x-3 gap-y-1.5 text-xs">
            {rows.map(([label, value]) => (
              <div key={label} className="flex items-center justify-between gap-2">
                <dt className="text-ink-faint">{label}</dt>
                <dd className="font-mono text-ink">{value}</dd>
              </div>
            ))}
          </dl>
          {info.category === 'SORTING' && (
            <p className="mt-2 text-xs text-ink-faint">
              Stable: <span className="text-ink">{info.stable ? 'Yes' : 'No'}</span>
            </p>
          )}
        </>
      ) : (
        <p className="mt-2 text-xs text-ink-faint">
          Start the backend to load complexity data.
        </p>
      )}
    </div>
  )
}
