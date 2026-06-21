import { AlertCircle, Loader2 } from 'lucide-react'
import { useVisualizerContext } from '../../context/VisualizerContext'
import PlaybackControls from '../PlaybackControls'
import ArrayStage from '../ArrayStage'
import Legend from '../Legend'

/**
 * Main stage: the playback controls, the row of numbered boxes for the current
 * step, a caption, and the color legend. All animation plays back steps the
 * backend computed — there is no algorithm logic here.
 */
export default function Canvas() {
  const { steps, currentStep, status, error } = useVisualizerContext()
  const hasTrace = steps.length > 0

  return (
    <main className="flex flex-1 flex-col bg-surface-sunken">
      <PlaybackControls />

      <div className="flex flex-1 flex-col items-center justify-center gap-6 p-8">
        {status === 'error' && <ErrorState message={error} />}

        {status === 'loading' && (
          <div className="flex items-center gap-2 text-sm text-ink-soft">
            <Loader2 size={16} className="animate-spin" />
            Computing steps…
          </div>
        )}

        {status !== 'loading' && status !== 'error' && !hasTrace && <EmptyState />}

        {hasTrace && (
          <>
            <ArrayStage step={currentStep} />
            <p className="max-w-xl text-center font-mono text-sm text-ink">
              {currentStep?.description}
            </p>
            <Legend />
          </>
        )}
      </div>
    </main>
  )
}

function EmptyState() {
  return (
    <div className="flex flex-col items-center gap-2 text-center">
      <p className="font-mono text-sm text-ink-soft">Pick an algorithm and hit Run</p>
      <p className="max-w-sm text-xs text-ink-faint">
        Choose an algorithm in the sidebar and press Run. The backend computes
        every step; this stage plays it back as colored, numbered boxes.
      </p>
    </div>
  )
}

function ErrorState({ message }) {
  return (
    <div className="flex max-w-md items-start gap-2 rounded-box border border-state-swapping/40 bg-state-swapping/10 px-4 py-3 text-sm text-ink">
      <AlertCircle size={16} className="mt-0.5 shrink-0 text-state-swapping" />
      <span>{message}</span>
    </div>
  )
}
