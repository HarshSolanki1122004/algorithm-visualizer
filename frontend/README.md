# Algorithm Visualizer

An interactive, step-by-step visualizer for sorting and searching algorithms.
Numbered boxes change color and animate through every comparison and swap, with
playback controls, speed control, and per-algorithm Big-O complexity cards.

> **Status:** Phase 1 complete — project skeleton, theming, and layout shell.
> Algorithms and interactivity are added phase by phase
> (see `PROJECT2_ALGORITHM_VISUALIZER.md`).

## Tech stack

- React 18 + Vite
- Tailwind CSS (class-based dark mode, CSS-variable theming)
- Framer Motion (box animations — added in Phase 3)
- lucide-react (icons)
- clsx

## Getting started

```bash
npm install
npm run dev
```

Then open the printed local URL (default `http://localhost:5173`).

## Verify Phase 1

- App runs and shows the three-region shell: header, sidebar, canvas, step log.
- The theme toggle (top right) switches light/dark and persists across reloads.
- The layout is responsive and respects `prefers-reduced-motion`.

## Project structure

```
src/
├── App.jsx                  layout shell
├── main.jsx                 entry point
├── index.css                Tailwind + theme tokens (light/dark)
├── hooks/
│   └── useTheme.js          theme state + localStorage persistence
├── components/layout/
│   ├── Header.jsx           title + theme toggle
│   ├── Sidebar.jsx          algorithm selector (Phase 5)
│   ├── Canvas.jsx           array stage + controls (Phases 3–4)
│   └── StepLogPanel.jsx     live step log (Phase 5)
├── algorithms/
│   ├── sorting/             bubble, selection, insertion, merge, quick (Phase 2)
│   └── searching/           linear, binary (Phase 2)
└── data/                    Big-O complexity data (Phase 5)
```

## Color system

Chrome (surfaces, text, brand accent) is driven by CSS variables that swap
between light and dark. The **functional box-state colors** have fixed meanings
in both themes:

| State | Color |
|---|---|
| Comparing | amber |
| Swapping | orange |
| Sorted | green |
| Pivot | purple |
| Found | teal |
