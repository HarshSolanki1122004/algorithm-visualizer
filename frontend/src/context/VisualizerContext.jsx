import { createContext, useContext } from 'react'
import { useVisualizer } from '../hooks/useVisualizer'

/**
 * Shares the single {@link useVisualizer} state across the layout so the
 * Canvas, Sidebar (F2), and Step Log (F2) all read and drive the same trace
 * without prop drilling.
 */
const VisualizerContext = createContext(null)

export function VisualizerProvider({ children }) {
  const visualizer = useVisualizer()
  return (
    <VisualizerContext.Provider value={visualizer}>
      {children}
    </VisualizerContext.Provider>
  )
}

export function useVisualizerContext() {
  const ctx = useContext(VisualizerContext)
  if (ctx === null) {
    throw new Error('useVisualizerContext must be used within a VisualizerProvider')
  }
  return ctx
}
