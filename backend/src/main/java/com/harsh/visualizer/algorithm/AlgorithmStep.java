package com.harsh.visualizer.algorithm;

import java.util.List;

/**
 * One immutable frame in an algorithm's execution trace — the core domain object
 * of the engine. Every comparison, swap, pivot selection, and sorted marker an
 * algorithm performs is captured as one {@code AlgorithmStep}.
 *
 * <p>This is deliberately separate from the {@code StepDto} response object: the
 * domain layer records steps with no knowledge of JSON, and the service layer
 * maps these into DTOs at the API boundary (Phase B4).</p>
 *
 * <p>The record is immutable — the list fields are defensively copied into
 * unmodifiable lists in the compact constructor, so a step can never be mutated
 * after an algorithm has emitted it.</p>
 *
 * @param array       snapshot of the array at this step
 * @param comparing   indices currently being compared
 * @param swapping    indices being swapped this step
 * @param sorted      indices now locked in their final sorted position
 * @param pivot       pivot index for quicksort, if any (null otherwise)
 * @param checking    index being inspected during a search (null otherwise)
 * @param found       index where the search target was found (null otherwise)
 * @param low         binary search: lower bound of the active window (null otherwise)
 * @param mid         binary search: midpoint being compared (null otherwise)
 * @param high        binary search: upper bound of the active window (null otherwise)
 * @param description human-readable explanation of this step
 */
public record AlgorithmStep(
        List<Integer> array,
        List<Integer> comparing,
        List<Integer> swapping,
        List<Integer> sorted,
        Integer pivot,
        Integer checking,
        Integer found,
        Integer low,
        Integer mid,
        Integer high,
        String description
) {

    /** Defensively copies the list fields into unmodifiable lists (null becomes empty). */
    public AlgorithmStep {
        array = copyOf(array);
        comparing = copyOf(comparing);
        swapping = copyOf(swapping);
        sorted = copyOf(sorted);
    }

    private static List<Integer> copyOf(List<Integer> source) {
        return source == null ? List.of() : List.copyOf(source);
    }

    /**
     * Starts a builder for a step. Callers (the {@link StepRecorder}) supply the
     * array snapshot, the indices already sorted, and a description; the optional
     * highlight fields default to empty/null.
     */
    public static Builder builder(List<Integer> array, List<Integer> sorted, String description) {
        return new Builder(array, sorted, description);
    }

    /**
     * Fluent builder for {@link AlgorithmStep}. Only the highlight fields relevant
     * to a given step need to be set; everything else stays empty or null and is
     * omitted from the eventual JSON.
     */
    public static final class Builder {
        private final List<Integer> array;
        private final List<Integer> sorted;
        private final String description;
        private List<Integer> comparing = List.of();
        private List<Integer> swapping = List.of();
        private Integer pivot;
        private Integer checking;
        private Integer found;
        private Integer low;
        private Integer mid;
        private Integer high;

        private Builder(List<Integer> array, List<Integer> sorted, String description) {
            this.array = array;
            this.sorted = sorted;
            this.description = description;
        }

        public Builder comparing(List<Integer> comparing) {
            this.comparing = comparing;
            return this;
        }

        public Builder swapping(List<Integer> swapping) {
            this.swapping = swapping;
            return this;
        }

        public Builder pivot(Integer pivot) {
            this.pivot = pivot;
            return this;
        }

        public Builder checking(Integer checking) {
            this.checking = checking;
            return this;
        }

        public Builder found(Integer found) {
            this.found = found;
            return this;
        }

        public Builder window(Integer low, Integer mid, Integer high) {
            this.low = low;
            this.mid = mid;
            this.high = high;
            return this;
        }

        public AlgorithmStep build() {
            return new AlgorithmStep(
                    array, comparing, swapping, sorted,
                    pivot, checking, found, low, mid, high, description);
        }
    }
}
