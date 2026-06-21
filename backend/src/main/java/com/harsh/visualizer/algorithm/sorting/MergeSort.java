package com.harsh.visualizer.algorithm.sorting;

import com.harsh.visualizer.algorithm.Algorithm;
import com.harsh.visualizer.algorithm.StepRecorder;
import com.harsh.visualizer.algorithm.VisualizationResult;
import com.harsh.visualizer.enums.AlgorithmType;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Merge sort as a step-recording strategy.
 *
 * <p>Top-down divide and conquer: recursively splits the range in half, then
 * merges the two sorted halves back together. Comparisons between the two halves
 * are recorded, and each element written back into place is recorded as an
 * overwrite so the merge is visible in the trace.</p>
 */
@Component
public class MergeSort implements Algorithm {

    @Override
    public AlgorithmType type() {
        return AlgorithmType.MERGE_SORT;
    }

    @Override
    public VisualizationResult run(int[] input, Integer target) {
        int[] arr = input.clone();
        StepRecorder recorder = new StepRecorder();

        if (arr.length > 1) {
            mergeSort(arr, 0, arr.length - 1, recorder);
        }

        for (int k = 0; k < arr.length; k++) {
            recorder.markSorted(k);
        }
        recorder.recordSnapshot(arr, "Array is fully sorted");
        return recorder.toSortResult();
    }

    private void mergeSort(int[] arr, int left, int right, StepRecorder recorder) {
        if (left >= right) {
            return;
        }
        int mid = left + (right - left) / 2;
        mergeSort(arr, left, mid, recorder);
        mergeSort(arr, mid + 1, right, recorder);
        merge(arr, left, mid, right, recorder);
    }

    private void merge(int[] arr, int left, int mid, int right, StepRecorder recorder) {
        int[] leftHalf = Arrays.copyOfRange(arr, left, mid + 1);
        int[] rightHalf = Arrays.copyOfRange(arr, mid + 1, right + 1);

        int i = 0;
        int j = 0;
        int k = left;

        while (i < leftHalf.length && j < rightHalf.length) {
            recorder.recordComparison(arr, left + i, mid + 1 + j,
                    "Merge: compare %d and %d".formatted(leftHalf[i], rightHalf[j]));
            if (leftHalf[i] <= rightHalf[j]) {
                recorder.recordOverwrite(arr, k, leftHalf[i],
                        "Place %d at index %d".formatted(leftHalf[i], k));
                i++;
            } else {
                recorder.recordOverwrite(arr, k, rightHalf[j],
                        "Place %d at index %d".formatted(rightHalf[j], k));
                j++;
            }
            k++;
        }
        while (i < leftHalf.length) {
            recorder.recordOverwrite(arr, k, leftHalf[i],
                    "Place remaining %d at index %d".formatted(leftHalf[i], k));
            i++;
            k++;
        }
        while (j < rightHalf.length) {
            recorder.recordOverwrite(arr, k, rightHalf[j],
                    "Place remaining %d at index %d".formatted(rightHalf[j], k));
            j++;
            k++;
        }
    }
}
