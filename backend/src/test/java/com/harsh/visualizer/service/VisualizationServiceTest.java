package com.harsh.visualizer.service;

import com.harsh.visualizer.algorithm.Algorithm;
import com.harsh.visualizer.algorithm.AlgorithmRegistry;
import com.harsh.visualizer.algorithm.searching.BinarySearch;
import com.harsh.visualizer.algorithm.searching.LinearSearch;
import com.harsh.visualizer.algorithm.sorting.BubbleSort;
import com.harsh.visualizer.algorithm.sorting.InsertionSort;
import com.harsh.visualizer.algorithm.sorting.MergeSort;
import com.harsh.visualizer.algorithm.sorting.QuickSort;
import com.harsh.visualizer.algorithm.sorting.SelectionSort;
import com.harsh.visualizer.dto.request.SearchRequest;
import com.harsh.visualizer.dto.request.SortRequest;
import com.harsh.visualizer.dto.response.StepDto;
import com.harsh.visualizer.dto.response.VisualizationResponse;
import com.harsh.visualizer.enums.AlgorithmType;
import com.harsh.visualizer.exception.InvalidInputException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the service's validation and domain→DTO mapping in isolation, with a
 * hand-built registry of all seven strategies and array bounds [2, 30].
 */
class VisualizationServiceTest {

    private VisualizationService service;

    @BeforeEach
    void setUp() {
        List<Algorithm> algorithms = List.of(
                new BubbleSort(), new SelectionSort(), new InsertionSort(),
                new MergeSort(), new QuickSort(), new LinearSearch(), new BinarySearch());
        service = new VisualizationService(new AlgorithmRegistry(algorithms), 2, 30);
    }

    // === Sort ================================================================

    @Test
    @DisplayName("sort returns the trace with summary counts and a null found flag")
    void sortProducesTrace() {
        VisualizationResponse response =
                service.sort(new SortRequest(AlgorithmType.BUBBLE_SORT, List.of(5, 2, 9, 1)));

        assertEquals(AlgorithmType.BUBBLE_SORT, response.algorithm());
        assertEquals(List.of(5, 2, 9, 1), response.initialArray());
        assertTrue(response.totalSteps() > 0);
        assertEquals(response.steps().size(), response.totalSteps());
        assertTrue(response.comparisons() > 0);
        assertNull(response.found(), "sorts carry no found flag");

        // final frame is the sorted array
        StepDto last = response.steps().get(response.steps().size() - 1);
        assertEquals(List.of(1, 2, 5, 9), last.array());
    }

    @Test
    @DisplayName("empty highlight arrays are mapped to null so JSON omits them")
    void emptyHighlightsBecomeNull() {
        VisualizationResponse response =
                service.sort(new SortRequest(AlgorithmType.BUBBLE_SORT, List.of(2, 1)));

        StepDto firstComparison = response.steps().get(0);
        assertEquals(List.of(0, 1), firstComparison.comparing());
        assertNull(firstComparison.swapping(), "no swap on a pure comparison frame");
    }

    @Test
    @DisplayName("sort rejects a searching algorithm")
    void sortRejectsSearchAlgorithm() {
        InvalidInputException ex = assertThrows(InvalidInputException.class,
                () -> service.sort(new SortRequest(AlgorithmType.BINARY_SEARCH, List.of(1, 2, 3))));
        assertTrue(ex.getMessage().contains("not a sorting"));
    }

    // === Search ==============================================================

    @Test
    @DisplayName("search reports found with the index in the trace")
    void searchFindsTarget() {
        VisualizationResponse response = service.search(
                new SearchRequest(AlgorithmType.BINARY_SEARCH, List.of(3, 12, 21, 45, 78), 45));

        assertTrue(response.found());
        assertEquals(0, response.swaps());
        StepDto last = response.steps().get(response.steps().size() - 1);
        assertEquals(3, last.found());
    }

    @Test
    @DisplayName("search reports not found")
    void searchReportsNotFound() {
        VisualizationResponse response = service.search(
                new SearchRequest(AlgorithmType.LINEAR_SEARCH, List.of(3, 12, 21), 99));
        assertFalse(response.found());
    }

    @Test
    @DisplayName("binary search rejects an unsorted array")
    void binarySearchRejectsUnsorted() {
        InvalidInputException ex = assertThrows(InvalidInputException.class,
                () -> service.search(
                        new SearchRequest(AlgorithmType.BINARY_SEARCH, List.of(5, 1, 3), 3)));
        assertTrue(ex.getMessage().contains("sorted"));
    }

    @Test
    @DisplayName("linear search accepts an unsorted array")
    void linearSearchAllowsUnsorted() {
        VisualizationResponse response = service.search(
                new SearchRequest(AlgorithmType.LINEAR_SEARCH, List.of(9, 1, 5, 3), 5));
        assertTrue(response.found());
    }

    @Test
    @DisplayName("search rejects a sorting algorithm")
    void searchRejectsSortAlgorithm() {
        assertThrows(InvalidInputException.class,
                () -> service.search(
                        new SearchRequest(AlgorithmType.BUBBLE_SORT, List.of(1, 2, 3), 2)));
    }

    // === Shared validation ===================================================

    @Test
    @DisplayName("rejects an array below the minimum size")
    void rejectsTooSmall() {
        InvalidInputException ex = assertThrows(InvalidInputException.class,
                () -> service.sort(new SortRequest(AlgorithmType.BUBBLE_SORT, List.of(1))));
        assertTrue(ex.getMessage().contains("between 2 and 30"));
    }

    @Test
    @DisplayName("rejects an array above the maximum size")
    void rejectsTooLarge() {
        List<Integer> big = java.util.stream.IntStream.rangeClosed(1, 31).boxed().toList();
        assertThrows(InvalidInputException.class,
                () -> service.sort(new SortRequest(AlgorithmType.BUBBLE_SORT, big)));
    }

    @Test
    @DisplayName("rejects null elements in the array")
    void rejectsNullElements() {
        List<Integer> withNull = java.util.Arrays.asList(1, null, 3);
        assertThrows(InvalidInputException.class,
                () -> service.sort(new SortRequest(AlgorithmType.BUBBLE_SORT, withNull)));
    }
}
