import clsx from 'clsx'
import { Play, Loader2 } from 'lucide-react'
import { useVisualizerContext } from '../../context/VisualizerContext'
import { isSearch } from '../../lib/stepState'
import ArrayInput from '../ArrayInput'
import BigOCard from '../BigOCard'

/**
 * Left rail: the algorithm selector (grouped by category, fed by GET
 * /algorithms), the array + target inputs, the Run button, and the Big-O
 * complexity card for the current algorithm.
 */
export default function Sidebar() {
  const { algorithms, algorithm, setAlgorithm, target, setTarget, selectedInfo, status, run } =
    useVisualizerContext()

  const sorting = algorithms.filter((a) => a.category === 'SORTING')
  const searching = algorithms.filter((a) => a.category === 'SEARCHING')
  const busy = status === 'loading'

  return (
    <aside className="flex w-72 shrink-0 flex-col gap-5 overflow-y-auto border-r border-edge bg-surface-raised p-4">
      <AlgorithmGroup
        label="Sorting"
        items={sorting}
        selected={algorithm}
        onSelect={setAlgorithm}
      />
      <AlgorithmGroup
        label="Searching"
        items={searching}
        selected={algorithm}
        onSelect={setAlgorithm}
      />

      <ArrayInput />

      {isSearch(algorithm) && (
        <label className="flex flex-col gap-1.5">
          <span className="px-1 text-xs font-semibold uppercase tracking-wider text-ink-faint">
            Target
          </span>
          <input
            type="number"
            value={target}
            onChange={(e) => setTarget(Number(e.target.value))}
            className="w-full rounded-box border border-edge bg-surface-base px-2.5 py-1.5 font-mono text-xs text-ink focus:border-brand"
          />
        </label>
      )}

      <button
        type="button"
        onClick={run}
        disabled={busy}
        className="flex items-center justify-center gap-2 rounded-box bg-brand py-2 text-sm font-semibold text-white transition-opacity hover:opacity-90 disabled:opacity-50"
      >
        {busy ? <Loader2 size={15} className="animate-spin" /> : <Play size={15} />}
        Run
      </button>

      <div className="mt-auto">
        <BigOCard info={selectedInfo} />
      </div>
    </aside>
  )
}

function AlgorithmGroup({ label, items, selected, onSelect }) {
  return (
    <div>
      <p className="mb-2 px-1 text-xs font-semibold uppercase tracking-wider text-ink-faint">
        {label}
      </p>
      <div className="flex flex-col gap-1">
        {items.map((a) => (
          <button
            key={a.type}
            type="button"
            onClick={() => onSelect(a.type)}
            className={clsx(
              'rounded-md px-2.5 py-1.5 text-left text-sm transition-colors',
              a.type === selected
                ? 'bg-brand-soft font-medium text-brand'
                : 'text-ink-soft hover:bg-surface-sunken hover:text-ink',
            )}
          >
            {a.displayName}
          </button>
        ))}
      </div>
    </div>
  )
}
