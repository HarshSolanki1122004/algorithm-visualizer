package com.harsh.visualizer.algorithm;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link StepRecorder} in isolation — no Spring context, no
 * algorithms. They pin down the contract the algorithms (Phase B3/B4) will rely on.
 */
class StepRecorderTest {

    @Nested
    @DisplayName("recordComparison")
    class RecordComparison {

        @Test
        @DisplayName("increments comparisons, captures the compared indices, leaves the array unchanged")
        void recordsComparison() {
            StepRecorder recorder = new StepRecorder();
            int[] arr = {5, 3, 8};

            recorder.recordComparison(arr, 0, 1, "Compare arr[0] and arr[1]");

            assertEquals(1, recorder.getComparisons());
            assertEquals(0, recorder.getSwaps());
            assertEquals(1, recorder.getSteps().size());

            AlgorithmStep step = recorder.getSteps().get(0);
            assertEquals(List.of(0, 1), step.comparing());
            assertEquals(List.of(5, 3, 8), step.array(), "array snapshot must be unchanged");
            assertTrue(step.swapping().isEmpty());
            assertEquals("Compare arr[0] and arr[1]", step.description());
            assertArrayUnchanged(arr, 5, 3, 8);
        }
    }

    @Nested
    @DisplayName("recordSwap")
    class RecordSwap {

        @Test
        @DisplayName("mutates the array, increments swaps, and snapshots the post-swap state")
        void recordsSwap() {
            StepRecorder recorder = new StepRecorder();
            int[] arr = {5, 3, 8};

            recorder.recordSwap(arr, 0, 1, "Swap arr[0] and arr[1]");

            assertEquals(0, recorder.getComparisons());
            assertEquals(1, recorder.getSwaps());

            AlgorithmStep step = recorder.getSteps().get(0);
            assertEquals(List.of(0, 1), step.swapping());
            assertEquals(List.of(3, 5, 8), step.array(), "snapshot must reflect the swap");
            assertArrayUnchanged(arr, 3, 5, 8);
        }
    }

    @Nested
    @DisplayName("recordOverwrite")
    class RecordOverwrite {

        @Test
        @DisplayName("writes the value, counts it as a swap, and highlights the index")
        void recordsOverwrite() {
            StepRecorder recorder = new StepRecorder();
            int[] arr = {5, 3, 8};

            recorder.recordOverwrite(arr, 2, 99, "Write 99 into arr[2]");

            assertEquals(1, recorder.getSwaps());
            AlgorithmStep step = recorder.getSteps().get(0);
            assertEquals(List.of(2), step.swapping());
            assertEquals(List.of(5, 3, 99), step.array());
            assertArrayUnchanged(arr, 5, 3, 99);
        }
    }

    @Nested
    @DisplayName("sorted tracking")
    class SortedTracking {

        @Test
        @DisplayName("markSorted accumulates across steps so every later frame carries the full set")
        void accumulatesSortedIndices() {
            StepRecorder recorder = new StepRecorder();
            int[] arr = {1, 2, 3};

            recorder.markSorted(2);
            recorder.recordComparison(arr, 0, 1, "first");
            recorder.markSorted(1);
            recorder.recordComparison(arr, 0, 1, "second");

            assertEquals(List.of(2), recorder.getSteps().get(0).sorted());
            assertEquals(List.of(2, 1), recorder.getSteps().get(1).sorted());
        }

        @Test
        @DisplayName("recordSorted marks the index and emits a frame for it")
        void recordSortedEmitsFrame() {
            StepRecorder recorder = new StepRecorder();
            int[] arr = {1, 2, 3};

            recorder.recordSorted(arr, 0, "arr[0] locked in place");

            assertEquals(1, recorder.getSteps().size());
            assertEquals(List.of(0), recorder.getSteps().get(0).sorted());
        }

        @Test
        @DisplayName("varargs markSorted records multiple indices")
        void marksMultiple() {
            StepRecorder recorder = new StepRecorder();
            int[] arr = {1, 2, 3};

            recorder.markSorted(0, 1, 2);
            recorder.recordSnapshot(arr, "all sorted");

            assertEquals(List.of(0, 1, 2), recorder.getSteps().get(0).sorted());
        }
    }

    @Nested
    @DisplayName("search recording")
    class SearchRecording {

        @Test
        @DisplayName("recordCheck increments comparisons and sets the checking index")
        void recordsCheck() {
            StepRecorder recorder = new StepRecorder();
            int[] arr = {3, 12, 21};

            recorder.recordCheck(arr, 1, "Check arr[1]");

            assertEquals(1, recorder.getComparisons());
            assertEquals(1, recorder.getSteps().get(0).checking());
        }

        @Test
        @DisplayName("recordSearchWindow captures the low/mid/high window")
        void recordsWindow() {
            StepRecorder recorder = new StepRecorder();
            int[] arr = {3, 12, 21, 45, 78};

            recorder.recordSearchWindow(arr, 0, 2, 4, "Inspect window");

            AlgorithmStep step = recorder.getSteps().get(0);
            assertEquals(1, recorder.getComparisons());
            assertEquals(0, step.low());
            assertEquals(2, step.mid());
            assertEquals(4, step.high());
            assertEquals(2, step.checking());
        }

        @Test
        @DisplayName("recordFound sets the found index without counting a comparison")
        void recordsFound() {
            StepRecorder recorder = new StepRecorder();
            int[] arr = {3, 12, 21};

            recorder.recordFound(arr, 2, "Found at arr[2]");

            assertEquals(0, recorder.getComparisons());
            assertEquals(2, recorder.getSteps().get(0).found());
        }
    }

    @Nested
    @DisplayName("pivot tracking")
    class PivotTracking {

        @Test
        @DisplayName("an active pivot is carried on every frame until cleared")
        void pivotPersistsUntilCleared() {
            StepRecorder recorder = new StepRecorder();
            int[] arr = {5, 3, 8};

            recorder.recordPivot(arr, 2, "pivot = arr[2]");
            recorder.recordComparison(arr, 0, 2, "compare with pivot");
            recorder.clearPivot();
            recorder.recordSnapshot(arr, "pivot placed");

            assertEquals(2, recorder.getSteps().get(0).pivot(), "pivot announced");
            assertEquals(2, recorder.getSteps().get(1).pivot(), "pivot still highlighted");
            assertNull(recorder.getSteps().get(2).pivot(), "pivot cleared");
        }

        @Test
        @DisplayName("no pivot field is set when none is active")
        void noPivotByDefault() {
            StepRecorder recorder = new StepRecorder();
            recorder.recordComparison(new int[]{1, 2}, 0, 1, "c");
            assertNull(recorder.getSteps().get(0).pivot());
        }
    }

    @Nested
    @DisplayName("results and immutability")
    class ResultsAndImmutability {

        @Test
        @DisplayName("toSortResult carries counts and a null found flag")
        void buildsSortResult() {
            StepRecorder recorder = new StepRecorder();
            int[] arr = {2, 1};
            recorder.recordComparison(arr, 0, 1, "c");
            recorder.recordSwap(arr, 0, 1, "s");

            VisualizationResult result = recorder.toSortResult();

            assertEquals(2, result.totalSteps());
            assertEquals(1, result.comparisons());
            assertEquals(1, result.swaps());
            assertNull(result.found());
        }

        @Test
        @DisplayName("toSearchResult carries the found flag")
        void buildsSearchResult() {
            StepRecorder recorder = new StepRecorder();
            int[] arr = {3, 12, 21};
            recorder.recordCheck(arr, 0, "c");

            VisualizationResult result = recorder.toSearchResult(true);

            assertEquals(1, result.comparisons());
            assertEquals(0, result.swaps());
            assertTrue(result.found());
        }

        @Test
        @DisplayName("getSteps returns an immutable list")
        void stepsAreImmutable() {
            StepRecorder recorder = new StepRecorder();
            int[] arr = {1};
            recorder.recordSnapshot(arr, "snap");

            List<AlgorithmStep> steps = recorder.getSteps();
            assertThrows(UnsupportedOperationException.class,
                    () -> steps.add(null));
        }

        @Test
        @DisplayName("a step's list fields are immutable")
        void stepFieldsAreImmutable() {
            StepRecorder recorder = new StepRecorder();
            int[] arr = {5, 3};
            recorder.recordComparison(arr, 0, 1, "c");

            AlgorithmStep step = recorder.getSteps().get(0);
            assertThrows(UnsupportedOperationException.class,
                    () -> step.array().add(0));
            assertThrows(UnsupportedOperationException.class,
                    () -> step.comparing().add(0));
        }

        @Test
        @DisplayName("a fresh recorder has no steps and zero counts")
        void startsEmpty() {
            StepRecorder recorder = new StepRecorder();

            assertTrue(recorder.getSteps().isEmpty());
            assertEquals(0, recorder.getComparisons());
            assertEquals(0, recorder.getSwaps());
            assertFalse(recorder.toSortResult().totalSteps() > 0);
        }
    }

    private static void assertArrayUnchanged(int[] actual, int... expected) {
        assertEquals(expected.length, actual.length);
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], actual[i], "arr[" + i + "]");
        }
    }
}
