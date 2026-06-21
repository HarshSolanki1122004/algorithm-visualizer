package com.harsh.visualizer.algorithm.sorting;

import com.harsh.visualizer.algorithm.Algorithm;
import com.harsh.visualizer.algorithm.StepRecorder;
import com.harsh.visualizer.algorithm.VisualizationResult;
import com.harsh.visualizer.enums.AlgorithmType;
import org.springframework.stereotype.Component;

/**
 * Selection sort as a step-recording strategy.
 *
 * <p>For each position, scans the unsorted suffix to find the minimum element,
 * then swaps it into place. One element is locked into its final sorted position
 * per pass.</p>
 */
@Component
public class SelectionSort implements Algorithm {

    @Override
    public AlgorithmType type() {
        return AlgorithmType.SELECTION_SORT;
    }

    @Override
    public VisualizationResult run(int[] input, Integer target) {
        int[] arr = input.clone();
        StepRecorder recorder = new StepRecorder();
        int n = arr.length;

        for (int i = 0; i < n - 1; i++) {
            int min = i;
            for (int j = i + 1; j < n; j++) {
                recorder.recordComparison(arr, min, j,
                        "Compare current min arr[%d]=%d with arr[%d]=%d".formatted(min, arr[min], j, arr[j]));
                if (arr[j] < arr[min]) {
                    min = j;
                }
            }
            if (min != i) {
                recorder.recordSwap(arr, i, min,
                        "Move smallest (%d) into position %d".formatted(arr[min], i));
            }
            recorder.markSorted(i);
        }
        if (n > 0) {
            recorder.markSorted(n - 1);
        }

        recorder.recordSnapshot(arr, "Array is fully sorted");
        return recorder.toSortResult();
    }
}
