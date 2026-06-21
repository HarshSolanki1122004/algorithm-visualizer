import ArrayBox from './ArrayBox'
import { boxState, inWindow } from '../lib/stepState'

/**
 * The row of numbered boxes for the current step. Reads each box's state and
 * window membership from the backend step; renders nothing algorithm-specific.
 */
export default function ArrayStage({ step }) {
  const array = step?.array ?? []

  return (
    <div className="flex flex-wrap items-end justify-center gap-2">
      {array.map((value, index) => (
        <ArrayBox
          key={index}
          value={value}
          index={index}
          state={boxState(step, index)}
          dimmed={!inWindow(step, index)}
        />
      ))}
    </div>
  )
}
