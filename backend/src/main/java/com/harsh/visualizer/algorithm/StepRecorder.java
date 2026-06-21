package com.harsh.visualizer.algorithm;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * The shared step-capture helper every algorithm leans on.
 *
 * <p>An algorithm creates one recorder, calls the {@code record*} / {@code mark*}
 * methods as it runs, and finishes by calling {@link #toSortResult()} or
 * {@link #toSearchResult(boolean)}. The recorder owns three concerns so the
 * algorithms themselves stay tiny and readable:</p>
 *
 * <ul>
 *   <li>snapshotting the array into an immutable {@link AlgorithmStep} per frame;</li>
 *   <li>tracking the running {@code comparisons} and {@code swaps} counts;</li>
 *   <li>accumulating the set of indices already locked in their sorted position,
 *       so every subsequent frame carries the full sorted-so-far highlight.</li>
 * </ul>
 *
 * <p>This class is intentionally <b>not</b> thread-safe and is meant to be used by
 * a single algorithm run; a fresh recorder is created per request.</p>
 */
public class StepRecorder {

    private final List<AlgorithmStep> steps = new ArrayList<>();
    private final Set<Integer> sortedIndices = new LinkedHashSet<>();
    private Integer activePivot;
    private int comparisons = 0;
    private int swaps = 0;

    // === Sorting-oriented recording =========================================

    /**
     * Records a comparison of {@code arr[i]} and {@code arr[j]}. Increments the
     * comparison counter; does not mutate the array.
     */
    public void recordComparison(int[] arr, int i, int j, String description) {
        comparisons++;
        steps.add(base(arr, description)
                .comparing(List.of(i, j))
                .build());
    }

    /**
     * Swaps {@code arr[i]} and {@code arr[j]} <em>and</em> records the resulting
     * frame, so the snapshot already reflects the moved elements. Increments the
     * swap counter.
     */
    public void recordSwap(int[] arr, int i, int j, String description) {
        int tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
        swaps++;
        steps.add(base(arr, description)
                .swapping(List.of(i, j))
                .build());
    }

    /**
     * Overwrites {@code arr[index]} with {@code value} and records the frame.
     * Used by algorithms that write values into place rather than swapping pairs
     * (e.g. insertion shifts, merge writes). Counted as a swap (a write).
     */
    public void recordOverwrite(int[] arr, int index, int value, String description) {
        arr[index] = value;
        swaps++;
        steps.add(base(arr, description)
                .swapping(List.of(index))
                .build());
    }

    /**
     * Sets the active pivot and emits a frame announcing it. The pivot stays
     * highlighted on every subsequent frame until {@link #clearPivot()} is called,
     * so quicksort's pivot is visible for the whole partition (B3.5).
     */
    public void recordPivot(int[] arr, int pivotIndex, String description) {
        setPivot(pivotIndex);
        steps.add(base(arr, description).build());
    }

    /** Marks an index as the active pivot carried by all following frames (no frame emitted). */
    public void setPivot(int pivotIndex) {
        this.activePivot = pivotIndex;
    }

    /** Stops highlighting a pivot — e.g. once it has been placed in its final position. */
    public void clearPivot() {
        this.activePivot = null;
    }

    /** Marks an index as locked in its final sorted position (state only — no frame). */
    public void markSorted(int index) {
        sortedIndices.add(index);
    }

    /** Marks several indices as sorted in one call. */
    public void markSorted(int... indices) {
        for (int index : indices) {
            sortedIndices.add(index);
        }
    }

    /**
     * Marks an index sorted and emits a frame highlighting the newly-sorted state,
     * for a clear "this element is now in place" beat in the animation.
     */
    public void recordSorted(int[] arr, int index, String description) {
        markSorted(index);
        steps.add(base(arr, description).build());
    }

    // === Searching-oriented recording =======================================

    /**
     * Records inspection of {@code arr[index]} against the target. Increments the
     * comparison counter (linear search, and each binary-search probe).
     */
    public void recordCheck(int[] arr, int index, String description) {
        comparisons++;
        steps.add(base(arr, description)
                .checking(index)
                .build());
    }

    /** Records the active {@code [low, mid, high]} window of a binary search. */
    public void recordSearchWindow(int[] arr, int low, int mid, int high, String description) {
        comparisons++;
        steps.add(base(arr, description)
                .checking(mid)
                .window(low, mid, high)
                .build());
    }

    /** Records the frame where the target was located at {@code index}. */
    public void recordFound(int[] arr, int index, String description) {
        steps.add(base(arr, description)
                .found(index)
                .build());
    }

    // === Generic / escape hatch =============================================

    /**
     * Records a plain snapshot of the array (current sorted highlight, no other
     * markers, no counter change). Useful for intro/outro frames.
     */
    public void recordSnapshot(int[] arr, String description) {
        steps.add(base(arr, description).build());
    }

    /**
     * Returns a builder pre-seeded with the array snapshot and the sorted-so-far
     * indices, for algorithms that need a frame this class doesn't directly model.
     * The caller is responsible for adding the result via {@link #add(AlgorithmStep)}.
     */
    public AlgorithmStep.Builder newStep(int[] arr, String description) {
        return base(arr, description);
    }

    /** Appends a fully-built custom step (companion to {@link #newStep}). */
    public void add(AlgorithmStep step) {
        steps.add(step);
    }

    // === Results / accessors =================================================

    /** Immutable view of every recorded step, in order. */
    public List<AlgorithmStep> getSteps() {
        return List.copyOf(steps);
    }

    public int getComparisons() {
        return comparisons;
    }

    public int getSwaps() {
        return swaps;
    }

    /** Builds the result for a sorting run (no {@code found} flag). */
    public VisualizationResult toSortResult() {
        return new VisualizationResult(getSteps(), comparisons, swaps, null);
    }

    /** Builds the result for a searching run, carrying whether the target was found. */
    public VisualizationResult toSearchResult(boolean found) {
        return new VisualizationResult(getSteps(), comparisons, swaps, found);
    }

    // === Internals ===========================================================

    /** Seeds a step builder with the array snapshot, sorted indices, and active pivot. */
    private AlgorithmStep.Builder base(int[] arr, String description) {
        return AlgorithmStep.builder(toList(arr), new ArrayList<>(sortedIndices), description)
                .pivot(activePivot);
    }

    private static List<Integer> toList(int[] arr) {
        List<Integer> list = new ArrayList<>(arr.length);
        for (int value : arr) {
            list.add(value);
        }
        return list;
    }
}
