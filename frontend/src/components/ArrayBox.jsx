import { motion } from 'framer-motion'
import clsx from 'clsx'

/**
 * One numbered box on the stage. Its color is driven entirely by the state the
 * backend reported for this index in the current step — the component holds no
 * algorithm knowledge, it just renders a state.
 */

const BORDER = {
  comparing: 'border-state-comparing',
  swapping: 'border-state-swapping',
  sorted: 'border-state-sorted',
  pivot: 'border-state-pivot',
  found: 'border-state-found',
}

const FILL = {
  comparing: 'bg-state-comparing/15',
  swapping: 'bg-state-swapping/20',
  sorted: 'bg-state-sorted/15',
  pivot: 'bg-state-pivot/20',
  found: 'bg-state-found/25',
}

// States that should "lift" off the row to draw the eye.
const ACTIVE = new Set(['comparing', 'swapping', 'pivot', 'found'])

export default function ArrayBox({ value, index, state, dimmed }) {
  const active = ACTIVE.has(state)

  return (
    <div className="flex flex-col items-center gap-1">
      <motion.div
        layout
        animate={{ scale: active ? 1.1 : 1, y: active ? -6 : 0 }}
        transition={{ type: 'spring', stiffness: 480, damping: 28 }}
        className={clsx(
          'flex h-12 w-12 items-center justify-center rounded-box border-2 font-mono text-sm font-semibold',
          'transition-colors duration-200',
          state ? BORDER[state] : 'border-edge',
          state ? FILL[state] : 'bg-surface-raised',
          state ? 'text-ink' : 'text-ink-soft',
          dimmed && !state && 'opacity-30',
        )}
      >
        {/* Re-keying on value gives a quick swap-in animation when it changes. */}
        <motion.span
          key={value}
          initial={{ opacity: 0, y: -8 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.18 }}
        >
          {value}
        </motion.span>
      </motion.div>
      <span className="font-mono text-[10px] text-ink-faint">{index}</span>
    </div>
  )
}
