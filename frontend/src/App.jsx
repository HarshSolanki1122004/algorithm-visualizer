import Header from './components/layout/Header'
import Sidebar from './components/layout/Sidebar'
import Canvas from './components/layout/Canvas'
import StepLogPanel from './components/layout/StepLogPanel'
import { VisualizerProvider } from './context/VisualizerContext'
import { useTheme } from './hooks/useTheme'

/**
 * Application shell.
 *
 *   ┌───────────────────────────────────────┐
 *   │ Header (title · theme toggle)          │
 *   ├──────────┬────────────────────────────┤
 *   │ Sidebar  │ Canvas (array stage)        │
 *   │ (select) │                             │
 *   ├──────────┴────────────────────────────┤
 *   │ Step Log                               │
 *   └───────────────────────────────────────┘
 */
export default function App() {
  const { theme, onToggleTheme } = useThemeBridge()

  return (
    <VisualizerProvider>
      <div className="flex h-screen flex-col overflow-hidden">
        <Header theme={theme} onToggleTheme={onToggleTheme} />
        <div className="flex min-h-0 flex-1">
          <Sidebar />
          <Canvas />
        </div>
        <StepLogPanel />
      </div>
    </VisualizerProvider>
  )
}

// Small adapter so the hook's { theme, toggleTheme } maps to the prop
// names the Header expects.
function useThemeBridge() {
  const { theme, toggleTheme } = useTheme()
  return { theme, onToggleTheme: toggleTheme }
}
