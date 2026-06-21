package com.harsh.visualizer.algorithm;

import com.harsh.visualizer.enums.AlgorithmType;

/**
 * Strategy interface implemented by every sorting and searching algorithm.
 *
 * <p>Each implementation runs over a copy of the input array, records every step
 * via a {@link StepRecorder}, and returns the complete {@link VisualizationResult}.
 * Because each strategy declares its own {@link #type()}, Spring can collect all
 * implementations into a {@code Map<AlgorithmType, Algorithm>} for lookup by the
 * service (Phase B3.6 / B4).</p>
 */
public interface Algorithm {

    /** The algorithm this strategy implements — its key in the strategy registry. */
    AlgorithmType type();

    /**
     * Runs the algorithm and captures its full step trace.
     *
     * @param array  the input array (implementations must not assume ownership;
     *               they should copy before mutating)
     * @param target the search target — required by searches, ignored by sorts
     *               (may be {@code null} for sorts)
     * @return the complete step-by-step result
     */
    VisualizationResult run(int[] array, Integer target);
}
