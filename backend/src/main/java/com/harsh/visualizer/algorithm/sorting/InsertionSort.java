package com.harsh.visualizer.algorithm.sorting;

import com.harsh.visualizer.algorithm.Algorithm;
import com.harsh.visualizer.algorithm.StepRecorder;
import com.harsh.visualizer.algorithm.VisualizationResult;
import com.harsh.visualizer.enums.AlgorithmType;
import org.springframework.stereotype.Component;

/**
 * Insertion sort as a step-recording strategy.
 *
 * <p>Grows a sorted prefix one element at a time: each new element is walked
 * leftward via adjacent swaps until it sits in the right place. Adjacent swaps
 * (rather than silent shifts) are used so the movement is visible frame by frame.
 * The sorted highlight is applied only at the end, because the prefix is "sorted
 * so far" but not yet in final positions until the whole pass completes.</p>
 */
@Component
public class InsertionSort implements Algorithm {

    @Override
    public AlgorithmType type() {
        return AlgorithmType.INSERTION_SORT;
    }

    @Override
    public VisualizationResult run(int[] input, Integer target) {
        int[] arr = input.clone();
        StepRecorder recorder = new StepRecorder();
        int n = arr.length;

        for (int i = 1; i < n; i++) {
            int j = i;
            while (j > 0) {
                recorder.recordComparison(arr, j - 1, j,
                        "Compare arr[%d]=%d and arr[%d]=%d".formatted(j - 1, arr[j - 1], j, arr[j]));
                if (arr[j - 1] > arr[j]) {
                    recorder.recordSwap(arr, j - 1, j,
                            "%d > %d — shift left".formatted(arr[j], arr[j - 1]));
                    j--;
                } else {
                    break;
                }
            }
        }

        for (int k = 0; k < n; k++) {
            recorder.markSorted(k);
        }
        recorder.recordSnapshot(arr, "Array is fully sorted");
        return recorder.toSortResult();
    }
}
