package com.harsh.visualizer.dto.response;

import com.harsh.visualizer.enums.AlgorithmType;

import java.util.List;

/**
 * Full response for a sort or search request: the complete step-by-step trace
 * plus summary counts the frontend shows on completion.
 *
 * @param algorithm    which algorithm was run
 * @param initialArray the input array before any steps
 * @param totalSteps   number of steps in the trace
 * @param comparisons  total comparisons performed
 * @param swaps        total swaps performed (0 for searches)
 * @param found        search only: whether the target was found (null for sorts)
 * @param steps        the ordered list of steps to play back
 */
public record VisualizationResponse(
        AlgorithmType algorithm,
        List<Integer> initialArray,
        int totalSteps,
        int comparisons,
        int swaps,
        Boolean found,
        List<StepDto> steps
) {
}
