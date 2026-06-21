package com.harsh.visualizer.algorithm;

import java.util.List;

/**
 * The domain-layer outcome of running an algorithm: the full ordered step trace
 * plus the running counts the {@link StepRecorder} accumulated.
 *
 * <p>Returned by {@link Algorithm#run(int[], Integer)}. The service layer maps
 * this into the API's {@code VisualizationResponse} (Phase B4), adding the
 * algorithm identity and the initial array.</p>
 *
 * @param steps       the ordered frames to play back (immutable copy)
 * @param comparisons total comparisons performed
 * @param swaps       total swaps performed (0 for searches)
 * @param found       search only: whether the target was found (null for sorts)
 */
public record VisualizationResult(
        List<AlgorithmStep> steps,
        int comparisons,
        int swaps,
        Boolean found
) {

    public VisualizationResult {
        steps = steps == null ? List.of() : List.copyOf(steps);
    }

    /** Convenience accessor mirroring the eventual API field. */
    public int totalSteps() {
        return steps.size();
    }
}
