import { useCallback, useEffect, useMemo, useState } from 'react'
import { fetchAlgorithms, runSearch, runSort } from '../api/visualizerApi'
import { isSearch } from '../lib/stepState'
import { FALLBACK_ALGORITHMS } from '../lib/algorithms'

const DEFAULT_ARRAY = [12, 45, 3, 78, 21, 9, 33]
const BASE_DELAY_MS = 700 // delay at 1× speed

/**
 * Central state for the visualizer: the chosen algorithm/array/target, the step
 * trace returned by the backend, and the playback position + transport state.
 *
 * status: 'idle' | 'loading' | 'ready' | 'error'  (playback is tracked by
 * `isPlaying`; `atEnd` means the playhead is on the last frame).
 */
export function useVisualizer() {
  const [algorithms, setAlgorithms] = useState(FALLBACK_ALGORITHMS)
  const [algorithm, setAlgorithm] = useState('BUBBLE_SORT')
  const [array, setArray] = useState(DEFAULT_ARRAY)
  const [target, setTarget] = useState(21)

  const [steps, setSteps] = useState([])
  const [stepIndex, setStepIndex] = useState(0)
  const [summary, setSummary] = useState(null)
  const [status, setStatus] = useState('idle')
  const [error, setError] = useState(null)

  const [isPlaying, setIsPlaying] = useState(false)
  const [speed, setSpeed] = useState(1)

  // Load the real algorithm list (with Big-O metadata) from the backend.
  useEffect(() => {
    fetchAlgorithms()
      .then((list) => {
        if (Array.isArray(list) && list.length > 0) setAlgorithms(list)
      })
      .catch(() => {
        /* keep the fallback list; the error surfaces on Run */
      })
  }, [])

  // Changing the inputs invalidates any existing trace.
  useEffect(() => {
    setSteps([])
    setStepIndex(0)
    setSummary(null)
    setIsPlaying(false)
    setStatus('idle')
    setError(null)
  }, [algorithm, array, target])

  const run = useCallback(async () => {
    setStatus('loading')
    setError(null)
    setSteps([])
    setStepIndex(0)
    setSummary(null)
    setIsPlaying(false)
    try {
      const result = isSearch(algorithm)
        ? await runSearch(algorithm, array, target)
        : await runSort(algorithm, array)

      setSteps(result.steps ?? [])
      setSummary({
        algorithm: result.algorithm,
        totalSteps: result.totalSteps,
        comparisons: result.comparisons,
        swaps: result.swaps,
        found: result.found,
      })
      setStepIndex(0)
      setStatus('ready')
      setIsPlaying(true) // auto-play once the trace arrives
    } catch (e) {
      setError(e.message)
      setStatus('error')
    }
  }, [algorithm, array, target])

  const atEnd = steps.length > 0 && stepIndex >= steps.length - 1

  // Drive playback: one timer per frame while playing.
  useEffect(() => {
    if (!isPlaying || status !== 'ready') return undefined
    if (atEnd) {
      setIsPlaying(false)
      return undefined
    }
    const delay = Math.max(60, Math.round(BASE_DELAY_MS / speed))
    const id = setTimeout(() => setStepIndex((i) => i + 1), delay)
    return () => clearTimeout(id)
  }, [isPlaying, status, stepIndex, steps.length, speed, atEnd])

  const play = useCallback(() => {
    setSteps((current) => {
      if (current.length === 0) return current
      // Restart from the top if we're sitting on the last frame.
      setStepIndex((i) => (i >= current.length - 1 ? 0 : i))
      setIsPlaying(true)
      return current
    })
  }, [])

  const pause = useCallback(() => setIsPlaying(false), [])
  const togglePlay = useCallback(() => (isPlaying ? pause() : play()), [isPlaying, play, pause])

  const stepForward = useCallback(() => {
    setIsPlaying(false)
    setStepIndex((i) => Math.min(i + 1, steps.length - 1))
  }, [steps.length])

  const stepBack = useCallback(() => {
    setIsPlaying(false)
    setStepIndex((i) => Math.max(i - 1, 0))
  }, [])

  const reset = useCallback(() => {
    setIsPlaying(false)
    setStepIndex(0)
  }, [])

  const currentStep = steps[stepIndex] ?? null
  const selectedInfo = useMemo(
    () => algorithms.find((a) => a.type === algorithm) ?? null,
    [algorithms, algorithm],
  )

  return useMemo(
    () => ({
      algorithms,
      algorithm,
      setAlgorithm,
      array,
      setArray,
      target,
      setTarget,
      selectedInfo,
      steps,
      stepIndex,
      currentStep,
      summary,
      status,
      error,
      isPlaying,
      atEnd,
      speed,
      setSpeed,
      run,
      play,
      pause,
      togglePlay,
      stepForward,
      stepBack,
      reset,
    }),
    [
      algorithms, algorithm, array, target, selectedInfo, steps, stepIndex, currentStep,
      summary, status, error, isPlaying, atEnd, speed, run, play, pause, togglePlay,
      stepForward, stepBack, reset,
    ],
  )
}
