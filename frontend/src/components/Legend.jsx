/**
 * Color key for the box states. Mirrors the semantic state tokens so the
 * meaning of each highlight is always on screen.
 */
const ITEMS = [
  { label: 'Comparing', dot: 'bg-state-comparing' },
  { label: 'Swapping', dot: 'bg-state-swapping' },
  { label: 'Pivot', dot: 'bg-state-pivot' },
  { label: 'Sorted', dot: 'bg-state-sorted' },
  { label: 'Found', dot: 'bg-state-found' },
]

export default function Legend() {
  return (
    <div className="flex flex-wrap items-center justify-center gap-x-4 gap-y-1.5">
      {ITEMS.map(({ label, dot }) => (
        <span key={label} className="flex items-center gap-1.5 text-xs text-ink-faint">
          <span className={`h-2.5 w-2.5 rounded-full ${dot}`} aria-hidden="true" />
          {label}
        </span>
      ))}
    </div>
  )
}
