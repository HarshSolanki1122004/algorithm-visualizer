package com.harsh.visualizer.dto.response;

import com.harsh.visualizer.enums.AlgorithmCategory;
import com.harsh.visualizer.enums.AlgorithmType;

/**
 * Metadata describing one algorithm, served by
 * {@code GET /api/visualize/algorithms} so the frontend's complexity cards
 * and selector are fully data-driven.
 */
public record AlgorithmInfo(
        AlgorithmType type,
        String displayName,
        AlgorithmCategory category,
        String bestCase,
        String averageCase,
        String worstCase,
        String spaceComplexity,
        boolean stable,
        String description
) {
    /** Builds the info record straight from the enum's metadata. */
    public static AlgorithmInfo from(AlgorithmType t) {
        return new AlgorithmInfo(
                t,
                t.getDisplayName(),
                t.getCategory(),
                t.getBestCase(),
                t.getAverageCase(),
                t.getWorstCase(),
                t.getSpaceComplexity(),
                t.isStable(),
                t.getDescription()
        );
    }
}
