/** @type {import('tailwindcss').Config} */
export default {
  darkMode: 'class',
  content: [
    './index.html',
    './src/**/*.{js,jsx}',
  ],
  theme: {
    extend: {
      colors: {
        // Surface / chrome — driven by CSS variables so light & dark
        // share one set of class names (see src/index.css).
        surface: {
          base: 'rgb(var(--surface-base) / <alpha-value>)',
          raised: 'rgb(var(--surface-raised) / <alpha-value>)',
          sunken: 'rgb(var(--surface-sunken) / <alpha-value>)',
        },
        edge: 'rgb(var(--edge) / <alpha-value>)',
        ink: {
          DEFAULT: 'rgb(var(--ink) / <alpha-value>)',
          soft: 'rgb(var(--ink-soft) / <alpha-value>)',
          faint: 'rgb(var(--ink-faint) / <alpha-value>)',
        },
        brand: {
          DEFAULT: 'rgb(var(--brand) / <alpha-value>)',
          soft: 'rgb(var(--brand-soft) / <alpha-value>)',
        },
        // Functional algorithm box states — semantic, fixed meanings.
        state: {
          comparing: 'rgb(var(--state-comparing) / <alpha-value>)',
          swapping: 'rgb(var(--state-swapping) / <alpha-value>)',
          sorted: 'rgb(var(--state-sorted) / <alpha-value>)',
          pivot: 'rgb(var(--state-pivot) / <alpha-value>)',
          found: 'rgb(var(--state-found) / <alpha-value>)',
        },
      },
      fontFamily: {
        sans: ['Inter', 'ui-sans-serif', 'system-ui', 'sans-serif'],
        mono: ['"JetBrains Mono"', 'ui-monospace', 'SFMono-Regular', 'Menlo', 'monospace'],
      },
      borderRadius: {
        box: '10px',
      },
    },
  },
  plugins: [],
}
