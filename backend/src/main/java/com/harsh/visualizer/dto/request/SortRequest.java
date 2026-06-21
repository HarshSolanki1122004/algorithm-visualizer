package com.harsh.visualizer.dto.request;

import com.harsh.visualizer.enums.AlgorithmType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * Request body for {@code POST /api/visualize/sort}.
 *
 * @param algorithm which sorting algorithm to run
 * @param array     the values to sort (size limits enforced in the service)
 */
public record SortRequest(

        @Schema(description = "Which sorting algorithm to run", example = "BUBBLE_SORT")
        @NotNull(message = "algorithm is required")
        AlgorithmType algorithm,

        @Schema(description = "Values to sort (2–30 integers)", example = "[12, 45, 3, 78, 21]")
        @NotEmpty(message = "array must not be empty")
        List<Integer> array
) {
}
