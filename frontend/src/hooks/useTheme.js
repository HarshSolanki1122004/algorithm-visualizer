import { useCallback, useEffect, useState } from 'react'

const STORAGE_KEY = 'av-theme'

/**
 * Light/dark theme state.
 *
 * Reads the initial value from localStorage (falling back to the OS
 * preference), keeps the `dark` class on <html> in sync, and persists
 * every change so the choice survives reloads. The inline script in
 * index.html applies the same value before first paint to avoid a flash.
 */
export function useTheme() {
  const [theme, setTheme] = useState(() => {
    if (typeof window === 'undefined') return 'light'
    const saved = localStorage.getItem(STORAGE_KEY)
    if (saved === 'light' || saved === 'dark') return saved
    return window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light'
  })

  useEffect(() => {
    const root = document.documentElement
    root.classList.toggle('dark', theme === 'dark')
    localStorage.setItem(STORAGE_KEY, theme)
  }, [theme])

  const toggleTheme = useCallback(() => {
    setTheme((t) => (t === 'dark' ? 'light' : 'dark'))
  }, [])

  return { theme, toggleTheme }
}
