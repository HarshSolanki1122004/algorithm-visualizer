import { useEffect, useRef } from 'react'
import { Terminal } from 'lucide-react'
import { useVisualizerContext } from '../../context/VisualizerContext'

/**
 * Bottom panel: a numbered, auto-scrolling log that streams each step up to the
 * current playhead, plus the completion summary (counts come straight from the
 * backend response).
 */
export default function StepLogPanel() {
  const { steps, stepIndex, summary } = useVisualizerContext()
  const endRef = useRef(null)

  const visible = steps.slice(0, stepIndex + 1)

  useEffect(() => {
    endRef.current?.scrollIntoView({ behavior: 'smooth', block: 'nearest' })
  }, [stepIndex, steps.length])

  return (
    <section className="flex h-40 shrink-0 flex-col border-t border-edge bg-surface-raised">
      <div className="flex items-center gap-2 border-b border-edge px-4 py-2">
        <Terminal size={14} className="text-ink-faint" />
        <h2 className="text-xs font-semibold uppercase tracking-wider text-ink-soft">Step Log</h2>
        {summary && (
          <span className="ml-auto font-mono text-xs text-ink-faint">
            {summary.comparisons} comparisons
            {summary.swaps > 0 && ` · ${summary.swaps} swaps`}
            {summary.found != null && ` · ${summary.found ? 'found' : 'not found'}`}
          </span>
        )}
      </div>

      <div className="flex-1 overflow-y-auto px-4 py-2">
        {steps.length === 0 ? (
          <p className="font-mono text-xs text-ink-faint">
            Steps will be recorded here as the algorithm runs.
          </p>
        ) : (
          <ol className="flex flex-col gap-0.5">
            {visible.map((step, i) => (
              <li
                key={i}
                className={
                  i === stepIndex
                    ? 'rounded bg-surface-sunken px-1.5 py-0.5 font-mono text-xs text-ink'
                    : 'px-1.5 py-0.5 font-mono text-xs text-ink-faint'
                }
              >
                <span className="text-ink-faint">{String(i + 1).padStart(3, '0')}</span>{' '}
                {step.description}
              </li>
            ))}
            <div ref={endRef} />
          </ol>
        )}
      </div>
    </section>
  )
}
