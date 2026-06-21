package com.harsh.visualizer.algorithm.sorting;

import com.harsh.visualizer.algorithm.Algorithm;
import com.harsh.visualizer.algorithm.StepRecorder;
import com.harsh.visualizer.algorithm.VisualizationResult;
import com.harsh.visualizer.enums.AlgorithmType;
import org.springframework.stereotype.Component;

/**
 * Bubble sort as a step-recording strategy.
 *
 * <p>Repeatedly walks the unsorted prefix, comparing adjacent pairs and swapping
 * those out of order; the largest remaining element "bubbles" to the end of each
 * pass and is marked sorted. Includes the early-exit optimisation: a pass with no
 * swaps means the array is already sorted (the O(n) best case).</p>
 */
@Component
public class BubbleSort implements Algorithm {

    @Override
    public AlgorithmType type() {
        return AlgorithmType.BUBBLE_SORT;
    }

    @Override
    public VisualizationResult run(int[] input, Integer target) {
        int[] arr = input.clone();
        StepRecorder recorder = new StepRecorder();
        int n = arr.length;

        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < n - 1 - i; j++) {
                recorder.recordComparison(arr, j, j + 1,
                        "Compare arr[%d]=%d and arr[%d]=%d".formatted(j, arr[j], j + 1, arr[j + 1]));
                if (arr[j] > arr[j + 1]) {
                    recorder.recordSwap(arr, j, j + 1,
                            "%d > %d — swap".formatted(arr[j + 1], arr[j]));
                    swapped = true;
                }
            }
            recorder.markSorted(n - 1 - i);
            if (!swapped) {
                break;
            }
        }

        markAllSorted(recorder, n);
        recorder.recordSnapshot(arr, "Array is fully sorted");
        return recorder.toSortResult();
    }

    private static void markAllSorted(StepRecorder recorder, int n) {
        for (int k = 0; k < n; k++) {
            recorder.markSorted(k);
        }
    }
}
