package com.harsh.visualizer.algorithm.searching;

import com.harsh.visualizer.algorithm.Algorithm;
import com.harsh.visualizer.algorithm.StepRecorder;
import com.harsh.visualizer.algorithm.VisualizationResult;
import com.harsh.visualizer.enums.AlgorithmType;
import org.springframework.stereotype.Component;

/**
 * Linear search as a step-recording strategy.
 *
 * <p>Walks the array left to right, recording each element it inspects, and stops
 * at the first match. Works on any array — sorted or not.</p>
 */
@Component
public class LinearSearch implements Algorithm {

    @Override
    public AlgorithmType type() {
        return AlgorithmType.LINEAR_SEARCH;
    }

    @Override
    public VisualizationResult run(int[] input, Integer target) {
        int[] arr = input.clone();
        StepRecorder recorder = new StepRecorder();
        int needle = target;

        for (int i = 0; i < arr.length; i++) {
            recorder.recordCheck(arr, i,
                    "Check arr[%d]=%d against target %d".formatted(i, arr[i], needle));
            if (arr[i] == needle) {
                recorder.recordFound(arr, i,
                        "Found target %d at index %d".formatted(needle, i));
                return recorder.toSearchResult(true);
            }
        }

        recorder.recordSnapshot(arr, "Reached the end — target %d not found".formatted(needle));
        return recorder.toSearchResult(false);
    }
}
