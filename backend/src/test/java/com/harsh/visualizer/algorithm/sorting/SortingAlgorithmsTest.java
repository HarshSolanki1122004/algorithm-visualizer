package com.harsh.visualizer.algorithm.sorting;

import com.harsh.visualizer.algorithm.Algorithm;
import com.harsh.visualizer.algorithm.AlgorithmStep;
import com.harsh.visualizer.algorithm.VisualizationResult;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Cross-algorithm contract tests for the five sorts (B3.7). Each sort is run over
 * a battery of array fixtures and must: produce the correctly sorted output, emit
 * a non-empty step list, keep every intermediate frame a permutation of the input,
 * and finish with all indices marked sorted.
 */
class SortingAlgorithmsTest {

    /** One instance of each sort strategy. */
    static Stream<Arguments> sorts() {
        return Stream.of(
                Arguments.of("BubbleSort", new BubbleSort()),
                Arguments.of("SelectionSort", new SelectionSort()),
                Arguments.of("InsertionSort", new InsertionSort()),
                Arguments.of("MergeSort", new MergeSort()),
                Arguments.of("QuickSort", new QuickSort()));
    }

    private static final int[][] FIXTURES = {
            {},
            {42},
            {2, 1},
            {1, 2, 3, 4, 5},
            {5, 4, 3, 2, 1},
            {5, 2, 9, 1, 5, 6},
            {3, 3, 3, 1, 1},
            {-3, 0, -1, 5, -9, 2},
            {10, -2, 33, 7, 7, 0, 100, -50, 12}
    };

    @ParameterizedTest(name = "{0} sorts every fixture correctly")
    @MethodSource("sorts")
    void sortsCorrectly(String name, Algorithm sort) {
        for (int[] fixture : FIXTURES) {
            int[] expected = fixture.clone();
            Arrays.sort(expected);

            VisualizationResult result = sort.run(fixture, null);
            int[] finalArray = lastArray(result);

            assertArrayEquals(expected, finalArray,
                    name + " failed on " + Arrays.toString(fixture));
        }
    }

    @ParameterizedTest(name = "{0} emits a non-empty, consistent trace")
    @MethodSource("sorts")
    void emitsConsistentSteps(String name, Algorithm sort) {
        for (int[] fixture : FIXTURES) {
            VisualizationResult result = sort.run(fixture, null);

            assertFalse(result.steps().isEmpty(),
                    name + " produced no steps for " + Arrays.toString(fixture));
            assertEquals(result.steps().size(), result.totalSteps());
            assertNull(result.found(), name + " is a sort — found must be null");

            // Every frame keeps the array's length, and the last frame is the
            // sorted permutation of the input.
            for (AlgorithmStep step : result.steps()) {
                assertEquals(fixture.length, step.array().size(),
                        name + " changed the array length mid-trace for "
                                + Arrays.toString(fixture));
            }
            int[] sortedInput = fixture.clone();
            Arrays.sort(sortedInput);
            int[] finalMultiset = lastArray(result);
            Arrays.sort(finalMultiset);
            assertArrayEquals(sortedInput, finalMultiset,
                    name + " final frame is not a permutation of the input for "
                            + Arrays.toString(fixture));
        }
    }

    /**
     * The swap-based sorts only ever exchange existing elements, so <em>every</em>
     * frame is a permutation of the input. (Merge sort is excluded: it overwrites
     * in place while merging from copies, so a single value can transiently appear
     * twice before its partner is written — a correct, expected merge state.)
     */
    @ParameterizedTest(name = "{0} preserves the multiset on every frame")
    @MethodSource("swapBasedSorts")
    void swapSortsPreservePermutationPerFrame(String name, Algorithm sort) {
        for (int[] fixture : FIXTURES) {
            int[] sortedInput = fixture.clone();
            Arrays.sort(sortedInput);
            for (AlgorithmStep step : sort.run(fixture, null).steps()) {
                assertArrayEquals(sortedInput, sortedMultiset(step.array()),
                        name + " frame is not a permutation of the input for "
                                + Arrays.toString(fixture));
            }
        }
    }

    static Stream<Arguments> swapBasedSorts() {
        return Stream.of(
                Arguments.of("BubbleSort", new BubbleSort()),
                Arguments.of("SelectionSort", new SelectionSort()),
                Arguments.of("InsertionSort", new InsertionSort()),
                Arguments.of("QuickSort", new QuickSort()));
    }

    @ParameterizedTest(name = "{0} marks all indices sorted on the final frame")
    @MethodSource("sorts")
    void finalFrameFullySorted(String name, Algorithm sort) {
        int[] fixture = {5, 2, 9, 1, 5, 6};
        VisualizationResult result = sort.run(fixture, null);

        List<Integer> sorted = result.steps().get(result.steps().size() - 1).sorted();
        for (int i = 0; i < fixture.length; i++) {
            assertTrue(sorted.contains(i),
                    name + " did not mark index " + i + " sorted");
        }
    }

    @ParameterizedTest(name = "{0} counts at least one comparison for a non-trivial array")
    @MethodSource("sorts")
    void recordsComparisons(String name, Algorithm sort) {
        VisualizationResult result = sort.run(new int[]{5, 2, 9, 1, 5, 6}, null);
        assertTrue(result.comparisons() >= 1,
                name + " recorded no comparisons");
    }

    @ParameterizedTest(name = "{0} does not mutate the caller's array")
    @MethodSource("sorts")
    void doesNotMutateInput(String name, Algorithm sort) {
        int[] fixture = {5, 2, 9, 1, 5, 6};
        int[] copy = fixture.clone();
        sort.run(fixture, null);
        assertArrayEquals(copy, fixture, name + " mutated the caller's array");
    }

    private static int[] lastArray(VisualizationResult result) {
        return toIntArray(result.steps().get(result.steps().size() - 1).array());
    }

    private static int[] sortedMultiset(List<Integer> list) {
        int[] arr = toIntArray(list);
        Arrays.sort(arr);
        return arr;
    }

    private static int[] toIntArray(List<Integer> list) {
        int[] arr = new int[list.size()];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = list.get(i);
        }
        return arr;
    }
}
