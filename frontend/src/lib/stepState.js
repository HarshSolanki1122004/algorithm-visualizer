/**
 * Pure helpers for interpreting a backend step. The backend decides *what*
 * happens at each step; these functions only decide *how to color it*.
 */

/** Algorithm types that use the /search endpoint (everything else is a sort). */
export const SEARCH_ALGORITHMS = new Set(['LINEAR_SEARCH', 'BINARY_SEARCH'])

export function isSearch(algorithm) {
  return SEARCH_ALGORITHMS.has(algorithm)
}

/**
 * The visual state of a single box at a given index for a step, by priority:
 * found › swapping › comparing/checking › pivot › sorted › none.
 *
 * @returns {'found'|'swapping'|'comparing'|'pivot'|'sorted'|null}
 */
export function boxState(step, index) {
  if (!step) return null
  if (step.found === index) return 'found'
  if (step.swapping?.includes(index)) return 'swapping'
  if (step.comparing?.includes(index) || step.checking === index) return 'comparing'
  if (step.pivot === index) return 'pivot'
  if (step.sorted?.includes(index)) return 'sorted'
  return null
}

/**
 * Whether an index is inside a binary-search window. Indices outside
 * `[low, high]` are dimmed to show they've been ruled out. Sorts (no window)
 * always return true.
 */
export function inWindow(step, index) {
  if (step?.low == null || step?.high == null) return true
  return index >= step.low && index <= step.high
}
