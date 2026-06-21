package com.harsh.visualizer.controller;

import com.harsh.visualizer.dto.request.SearchRequest;
import com.harsh.visualizer.dto.request.SortRequest;
import com.harsh.visualizer.dto.response.AlgorithmInfo;
import com.harsh.visualizer.dto.response.VisualizationResponse;
import com.harsh.visualizer.enums.AlgorithmType;
import com.harsh.visualizer.exception.ApiError;
import com.harsh.visualizer.service.VisualizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * REST endpoints for the visualizer.
 *
 * <p>The three endpoints are the entire public surface of the engine: run a sort,
 * run a search, or list the supported algorithms. Each delegates to
 * {@link VisualizationService}, which validates input, selects the algorithm
 * strategy, runs it, and returns the full step trace. Errors are rendered as a
 * consistent {@link ApiError} body by the global exception handler.</p>
 */
@RestController
@RequestMapping("/api/visualize")
@Tag(name = "Visualization", description = "Compute step-by-step algorithm traces")
public class VisualizationController {

    private final VisualizationService service;

    public VisualizationController(VisualizationService service) {
        this.service = service;
    }

    @Operation(
            summary = "Run a sorting algorithm and return its full step trace",
            description = "Sorts the given array with the chosen algorithm and returns every "
                    + "comparison, swap, and sorted marker as an ordered list of steps, plus "
                    + "summary counts. The array must hold 2–30 integers.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Step trace computed"),
            @ApiResponse(responseCode = "400",
                    description = "Invalid request — e.g. empty/oversized array, or a searching "
                            + "algorithm sent to this endpoint",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PostMapping("/sort")
    public VisualizationResponse sort(@Valid @RequestBody SortRequest request) {
        return service.sort(request);
    }

    @Operation(
            summary = "Run a searching algorithm and return its full step trace",
            description = "Searches the given array for the target with the chosen algorithm. "
                    + "Binary search requires the array to be sorted ascending; linear search "
                    + "works on any order. Returns each inspected index (and the binary-search "
                    + "window) as steps, plus whether the target was found.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Step trace computed"),
            @ApiResponse(responseCode = "400",
                    description = "Invalid request — e.g. missing target, a sorting algorithm sent "
                            + "to this endpoint, or an unsorted array for binary search",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PostMapping("/search")
    public VisualizationResponse search(@Valid @RequestBody SearchRequest request) {
        return service.search(request);
    }

    @Operation(
            summary = "List all supported algorithms with their Big-O metadata",
            description = "Returns every sorting and searching algorithm the engine supports, "
                    + "along with its display name, category, and best/average/worst/space "
                    + "complexity. The frontend's selector and complexity cards are driven "
                    + "entirely by this response.")
    @ApiResponse(responseCode = "200", description = "List of supported algorithms")
    @GetMapping("/algorithms")
    public List<AlgorithmInfo> algorithms() {
        return Arrays.stream(AlgorithmType.values())
                .map(AlgorithmInfo::from)
                .toList();
    }
}
