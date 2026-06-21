package com.harsh.visualizer.algorithm.searching;

import com.harsh.visualizer.algorithm.Algorithm;
import com.harsh.visualizer.algorithm.StepRecorder;
import com.harsh.visualizer.algorithm.VisualizationResult;
import com.harsh.visualizer.enums.AlgorithmType;
import org.springframework.stereotype.Component;

/**
 * Binary search as a step-recording strategy.
 *
 * <p>Repeatedly halves the active {@code [low, high]} window, recording the
 * window and the midpoint it probes each step. <b>Assumes the input is sorted
 * ascending</b> — the service enforces that precondition before calling this
 * (an unsorted array is rejected with HTTP 400).</p>
 */
@Component
public class BinarySearch implements Algorithm {

    @Override
    public AlgorithmType type() {
        return AlgorithmType.BINARY_SEARCH;
    }

    @Override
    public VisualizationResult run(int[] input, Integer target) {
        int[] arr = input.clone();
        StepRecorder recorder = new StepRecorder();
        int needle = target;

        int low = 0;
        int high = arr.length - 1;
        while (low <= high) {
            int mid = low + (high - low) / 2;
            recorder.recordSearchWindow(arr, low, mid, high,
                    "Window [%d..%d], probe arr[%d]=%d vs target %d"
                            .formatted(low, high, mid, arr[mid], needle));
            if (arr[mid] == needle) {
                recorder.recordFound(arr, mid,
                        "Found target %d at index %d".formatted(needle, mid));
                return recorder.toSearchResult(true);
            } else if (arr[mid] < needle) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        recorder.recordSnapshot(arr, "Window empty — target %d not found".formatted(needle));
        return recorder.toSearchResult(false);
    }
}
