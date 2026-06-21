package com.harsh.visualizer.algorithm;

import com.harsh.visualizer.algorithm.sorting.BubbleSort;
import com.harsh.visualizer.algorithm.sorting.InsertionSort;
import com.harsh.visualizer.algorithm.sorting.MergeSort;
import com.harsh.visualizer.algorithm.sorting.QuickSort;
import com.harsh.visualizer.algorithm.sorting.SelectionSort;
import com.harsh.visualizer.enums.AlgorithmType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the registry's indexing logic in isolation (no Spring context — full
 * auto-wiring is exercised by the Phase B4 integration test).
 */
class AlgorithmRegistryTest {

    private static List<Algorithm> allSorts() {
        return List.of(new BubbleSort(), new SelectionSort(),
                new InsertionSort(), new MergeSort(), new QuickSort());
    }

    @Test
    void indexesEverySortByType() {
        AlgorithmRegistry registry = new AlgorithmRegistry(allSorts());

        assertEquals(5, registry.asMap().size());
        assertInstanceOf(BubbleSort.class, registry.get(AlgorithmType.BUBBLE_SORT));
        assertInstanceOf(QuickSort.class, registry.get(AlgorithmType.QUICK_SORT));
    }

    @Test
    void findReturnsEmptyForUnregisteredType() {
        AlgorithmRegistry registry = new AlgorithmRegistry(allSorts());
        assertTrue(registry.find(AlgorithmType.BINARY_SEARCH).isEmpty());
    }

    @Test
    void getThrowsForUnregisteredType() {
        AlgorithmRegistry registry = new AlgorithmRegistry(allSorts());
        assertThrows(IllegalArgumentException.class,
                () -> registry.get(AlgorithmType.LINEAR_SEARCH));
    }

    @Test
    void rejectsDuplicateRegistrations() {
        List<Algorithm> withDuplicate = List.of(new BubbleSort(), new BubbleSort());
        assertThrows(IllegalStateException.class,
                () -> new AlgorithmRegistry(withDuplicate));
    }

    @Test
    void registryMapIsImmutable() {
        AlgorithmRegistry registry = new AlgorithmRegistry(allSorts());
        assertThrows(UnsupportedOperationException.class,
                () -> registry.asMap().clear());
    }
}
