package com.harsh.visualizer.dto.request;

import com.harsh.visualizer.enums.AlgorithmType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * Request body for {@code POST /api/visualize/search}.
 *
 * @param algorithm which searching algorithm to run
 * @param array     the values to search within
 * @param target    the value to search for
 */
public record SearchRequest(

        @Schema(description = "Which searching algorithm to run", example = "BINARY_SEARCH")
        @NotNull(message = "algorithm is required")
        AlgorithmType algorithm,

        @Schema(description = "Values to search within (must be sorted ascending for binary search)",
                example = "[3, 12, 21, 45, 78]")
        @NotEmpty(message = "array must not be empty")
        List<Integer> array,

        @Schema(description = "The value to search for", example = "45")
        @NotNull(message = "target is required for search")
        Integer target
) {
}
