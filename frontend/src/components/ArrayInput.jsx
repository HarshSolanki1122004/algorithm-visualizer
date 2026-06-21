import { useEffect, useState } from 'react'
import { Shuffle } from 'lucide-react'
import { useVisualizerContext } from '../context/VisualizerContext'

/**
 * Array editor: type a comma/space separated list (committed on blur or Enter)
 * or generate a random one. Binary search needs sorted input, so a random array
 * is sorted for it; a manually-entered unsorted array is left as-is so the
 * backend's "must be sorted" rule is visible.
 */
export default function ArrayInput() {
  const { array, setArray, algorithm } = useVisualizerContext()
  const [text, setText] = useState(array.join(', '))
  const [error, setError] = useState(null)

  // Resync the field when the array changes elsewhere (e.g. randomize).
  useEffect(() => {
    setText(array.join(', '))
    setError(null)
  }, [array])

  const commit = () => {
    const parts = text.split(/[,\s]+/).filter(Boolean)
    const nums = parts.map(Number)
    if (nums.some((n) => !Number.isInteger(n))) {
      setError('Whole numbers only')
      return
    }
    if (nums.length < 2 || nums.length > 30) {
      setError('Use 2–30 numbers')
      return
    }
    setError(null)
    setArray(nums)
  }

  const randomize = () => {
    let values = Array.from({ length: 7 }, () => Math.floor(Math.random() * 99) + 1)
    if (algorithm === 'BINARY_SEARCH') values = [...values].sort((a, b) => a - b)
    setArray(values)
  }

  return (
    <div>
      <div className="mb-2 flex items-center justify-between">
        <p className="px-1 text-xs font-semibold uppercase tracking-wider text-ink-faint">Array</p>
        <button
          type="button"
          onClick={randomize}
          className="flex items-center gap-1 text-xs text-ink-soft transition-colors hover:text-brand"
        >
          <Shuffle size={12} />
          Random
        </button>
      </div>
      <input
        value={text}
        onChange={(e) => setText(e.target.value)}
        onBlur={commit}
        onKeyDown={(e) => e.key === 'Enter' && commit()}
        spellCheck={false}
        className="w-full rounded-box border border-edge bg-surface-base px-2.5 py-1.5 font-mono text-xs text-ink focus:border-brand"
        aria-label="Array values"
      />
      {error && <p className="mt-1 text-xs text-state-swapping">{error}</p>}
    </div>
  )
}
