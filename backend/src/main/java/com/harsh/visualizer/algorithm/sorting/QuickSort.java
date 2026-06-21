package com.harsh.visualizer.algorithm.sorting;

import com.harsh.visualizer.algorithm.Algorithm;
import com.harsh.visualizer.algorithm.StepRecorder;
import com.harsh.visualizer.algorithm.VisualizationResult;
import com.harsh.visualizer.enums.AlgorithmType;
import org.springframework.stereotype.Component;

/**
 * Quick sort as a step-recording strategy, using Lomuto partitioning with the
 * last element of each range as the pivot.
 *
 * <p>Each partition announces its pivot, which then stays highlighted on every
 * frame of that partition (via the recorder's active-pivot state) until the pivot
 * is swapped into its final position and marked sorted.</p>
 */
@Component
public class QuickSort implements Algorithm {

    @Override
    public AlgorithmType type() {
        return AlgorithmType.QUICK_SORT;
    }

    @Override
    public VisualizationResult run(int[] input, Integer target) {
        int[] arr = input.clone();
        StepRecorder recorder = new StepRecorder();

        quickSort(arr, 0, arr.length - 1, recorder);

        for (int k = 0; k < arr.length; k++) {
            recorder.markSorted(k);
        }
        recorder.recordSnapshot(arr, "Array is fully sorted");
        return recorder.toSortResult();
    }

    private void quickSort(int[] arr, int low, int high, StepRecorder recorder) {
        if (low > high) {
            return;
        }
        if (low == high) {
            recorder.markSorted(low);
            return;
        }
        int pivotIndex = partition(arr, low, high, recorder);
        recorder.markSorted(pivotIndex);
        quickSort(arr, low, pivotIndex - 1, recorder);
        quickSort(arr, pivotIndex + 1, high, recorder);
    }

    private int partition(int[] arr, int low, int high, StepRecorder recorder) {
        int pivot = arr[high];
        recorder.recordPivot(arr, high,
                "Choose pivot arr[%d]=%d".formatted(high, pivot));

        int i = low - 1;
        for (int j = low; j < high; j++) {
            recorder.recordComparison(arr, j, high,
                    "Compare arr[%d]=%d with pivot %d".formatted(j, arr[j], pivot));
            if (arr[j] < pivot) {
                i++;
                if (i != j) {
                    recorder.recordSwap(arr, i, j,
                            "%d < pivot — move left".formatted(arr[j]));
                }
            }
        }
        if (i + 1 != high) {
            recorder.recordSwap(arr, i + 1, high,
                    "Place pivot %d at index %d".formatted(pivot, i + 1));
        }
        recorder.clearPivot();
        return i + 1;
    }
}
