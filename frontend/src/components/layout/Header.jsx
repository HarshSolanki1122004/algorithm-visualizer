import { Moon, Sun, Binary } from 'lucide-react'

/**
 * Top bar: product mark on the left, theme toggle on the right.
 */
export default function Header({ theme, onToggleTheme }) {
  return (
    <header className="flex items-center justify-between border-b border-edge bg-surface-raised px-5 py-3">
      <div className="flex items-center gap-2.5">
        <span className="flex h-8 w-8 items-center justify-center rounded-box bg-brand-soft text-brand">
          <Binary size={18} strokeWidth={2.25} />
        </span>
        <div className="leading-tight">
          <h1 className="font-mono text-base font-bold tracking-tight text-ink">
            Algorithm Visualizer
          </h1>
          <p className="text-xs text-ink-faint">Sorting &amp; searching, one step at a time</p>
        </div>
      </div>

      <button
        type="button"
        onClick={onToggleTheme}
        aria-label={theme === 'dark' ? 'Switch to light mode' : 'Switch to dark mode'}
        className="flex h-9 w-9 items-center justify-center rounded-box border border-edge bg-surface-base text-ink-soft transition-colors hover:text-ink hover:border-brand"
      >
        {theme === 'dark' ? <Sun size={17} /> : <Moon size={17} />}
      </button>
    </header>
  )
}
