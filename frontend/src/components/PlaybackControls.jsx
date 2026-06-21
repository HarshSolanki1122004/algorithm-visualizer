import { Play, Pause, ChevronLeft, ChevronRight, RotateCcw } from 'lucide-react'
import clsx from 'clsx'
import { useVisualizerContext } from '../context/VisualizerContext'

/**
 * Transport bar over the step trace: reset · step back · play/pause · step
 * forward · speed. Pure UI — it only moves the playhead through steps the
 * backend already computed.
 */
export default function PlaybackControls() {
  const { steps, stepIndex, isPlaying, togglePlay, stepBack, stepForward, reset, speed, setSpeed } =
    useVisualizerContext()

  const empty = steps.length === 0
  const atStart = stepIndex === 0
  const atEnd = stepIndex >= steps.length - 1

  return (
    <div className="flex flex-wrap items-center gap-2 border-b border-edge bg-surface-raised px-5 py-3">
      <IconButton label="Reset to start" onClick={reset} disabled={empty || atStart}>
        <RotateCcw size={16} />
      </IconButton>
      <IconButton label="Previous step" onClick={stepBack} disabled={empty || atStart}>
        <ChevronLeft size={18} />
      </IconButton>

      <button
        type="button"
        onClick={togglePlay}
        disabled={empty}
        aria-label={isPlaying ? 'Pause' : 'Play'}
        className="flex h-9 w-9 items-center justify-center rounded-box bg-brand text-white transition-opacity hover:opacity-90 disabled:opacity-40"
      >
        {isPlaying ? <Pause size={16} /> : <Play size={16} />}
      </button>

      <IconButton label="Next step" onClick={stepForward} disabled={empty || atEnd}>
        <ChevronRight size={18} />
      </IconButton>

      <label className="ml-3 flex items-center gap-2 text-xs text-ink-soft">
        Speed
        <input
          type="range"
          min="0.25"
          max="4"
          step="0.25"
          value={speed}
          onChange={(e) => setSpeed(Number(e.target.value))}
          className="accent-brand"
          aria-label="Playback speed"
        />
        <span className="w-8 font-mono text-ink">{speed}×</span>
      </label>

      <span className="ml-auto font-mono text-xs text-ink-faint">
        {empty ? '—' : `Step ${stepIndex + 1} / ${steps.length}`}
      </span>
    </div>
  )
}

function IconButton({ children, label, onClick, disabled }) {
  return (
    <button
      type="button"
      onClick={onClick}
      disabled={disabled}
      aria-label={label}
      className={clsx(
        'flex h-9 w-9 items-center justify-center rounded-box border border-edge bg-surface-base text-ink-soft',
        'transition-colors hover:border-brand hover:text-ink disabled:opacity-40 disabled:hover:border-edge disabled:hover:text-ink-soft',
      )}
    >
      {children}
    </button>
  )
}
