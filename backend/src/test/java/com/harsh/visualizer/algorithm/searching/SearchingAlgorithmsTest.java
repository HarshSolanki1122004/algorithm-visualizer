package com.harsh.visualizer.algorithm.searching;

import com.harsh.visualizer.algorithm.AlgorithmStep;
import com.harsh.visualizer.algorithm.VisualizationResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the two search strategies (B4.1 / B4.2), exercised directly
 * without Spring.
 */
class SearchingAlgorithmsTest {

    @Nested
    @DisplayName("LinearSearch")
    class Linear {

        private final LinearSearch search = new LinearSearch();

        @Test
        @DisplayName("finds a target and reports its index on the final frame")
        void findsTarget() {
            VisualizationResult result = search.run(new int[]{4, 8, 15, 16, 23}, 15);

            assertTrue(result.found());
            AlgorithmStep last = lastStep(result);
            assertEquals(2, last.found());
            assertEquals(3, result.comparisons(), "checked indices 0,1,2");
            assertEquals(0, result.swaps());
        }

        @Test
        @DisplayName("reports not found and scans the whole array")
        void reportsNotFound() {
            VisualizationResult result = search.run(new int[]{4, 8, 15}, 99);

            assertFalse(result.found());
            assertEquals(3, result.comparisons());
            assertFalse(result.steps().isEmpty());
        }

        @Test
        @DisplayName("stops at the first match")
        void stopsAtFirstMatch() {
            VisualizationResult result = search.run(new int[]{7, 7, 7}, 7);
            assertEquals(1, result.comparisons());
            assertEquals(0, lastStep(result).found());
        }

        @Test
        @DisplayName("works on an unsorted array")
        void worksUnsorted() {
            VisualizationResult result = search.run(new int[]{9, 1, 5, 3}, 5);
            assertTrue(result.found());
            assertEquals(2, lastStep(result).found());
        }
    }

    @Nested
    @DisplayName("BinarySearch")
    class Binary {

        private final BinarySearch search = new BinarySearch();

        @Test
        @DisplayName("finds a target in a sorted array using fewer comparisons than n")
        void findsTarget() {
            int[] arr = {3, 12, 21, 45, 78};
            VisualizationResult result = search.run(arr, 45);

            assertTrue(result.found());
            assertEquals(3, lastStep(result).found());
            assertTrue(result.comparisons() <= 3,
                    "binary search should not probe all 5 elements");
        }

        @Test
        @DisplayName("records the low/mid/high window on each probe")
        void recordsWindow() {
            VisualizationResult result = search.run(new int[]{3, 12, 21, 45, 78}, 78);

            AlgorithmStep first = result.steps().get(0);
            assertNotNull(first.low());
            assertNotNull(first.mid());
            assertNotNull(first.high());
            assertEquals(0, first.low());
            assertEquals(4, first.high());
        }

        @Test
        @DisplayName("reports not found for an absent target")
        void reportsNotFound() {
            VisualizationResult result = search.run(new int[]{3, 12, 21, 45, 78}, 50);
            assertFalse(result.found());
        }

        @Test
        @DisplayName("finds the first and last elements")
        void findsEdges() {
            int[] arr = {1, 2, 3, 4, 5, 6, 7};
            assertTrue(search.run(arr, 1).found());
            assertTrue(search.run(arr, 7).found());
        }
    }

    private static AlgorithmStep lastStep(VisualizationResult result) {
        List<AlgorithmStep> steps = result.steps();
        return steps.get(steps.size() - 1);
    }
}
