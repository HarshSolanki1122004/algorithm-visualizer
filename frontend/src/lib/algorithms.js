/**
 * Static fallback list of algorithms for the selector, used only when the
 * backend can't be reached. When the backend is up, the real list (with full
 * Big-O metadata) comes from GET /algorithms — the frontend never hardcodes
 * the complexity data itself.
 */
export const FALLBACK_ALGORITHMS = [
  { type: 'BUBBLE_SORT', displayName: 'Bubble Sort', category: 'SORTING' },
  { type: 'SELECTION_SORT', displayName: 'Selection Sort', category: 'SORTING' },
  { type: 'INSERTION_SORT', displayName: 'Insertion Sort', category: 'SORTING' },
  { type: 'MERGE_SORT', displayName: 'Merge Sort', category: 'SORTING' },
  { type: 'QUICK_SORT', displayName: 'Quick Sort', category: 'SORTING' },
  { type: 'LINEAR_SEARCH', displayName: 'Linear Search', category: 'SEARCHING' },
  { type: 'BINARY_SEARCH', displayName: 'Binary Search', category: 'SEARCHING' },
]
