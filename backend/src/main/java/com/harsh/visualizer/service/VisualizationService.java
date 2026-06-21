package com.harsh.visualizer.service;

import com.harsh.visualizer.algorithm.Algorithm;
import com.harsh.visualizer.algorithm.AlgorithmRegistry;
import com.harsh.visualizer.algorithm.AlgorithmStep;
import com.harsh.visualizer.algorithm.VisualizationResult;
import com.harsh.visualizer.dto.request.SearchRequest;
import com.harsh.visualizer.dto.request.SortRequest;
import com.harsh.visualizer.dto.response.StepDto;
import com.harsh.visualizer.dto.response.VisualizationResponse;
import com.harsh.visualizer.enums.AlgorithmType;
import com.harsh.visualizer.exception.InvalidInputException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Application service tying a request to the right strategy: it validates the
 * input, picks the algorithm from the {@link AlgorithmRegistry}, runs it, and
 * maps the domain {@link VisualizationResult} into the API's
 * {@link VisualizationResponse} (the domain ↔ DTO boundary).
 */
@Service
public class VisualizationService {

    private final AlgorithmRegistry registry;
    private final int minArraySize;
    private final int maxArraySize;

    public VisualizationService(
            AlgorithmRegistry registry,
            @Value("${visualizer.min-array-size}") int minArraySize,
            @Value("${visualizer.max-array-size}") int maxArraySize) {
        this.registry = registry;
        this.minArraySize = minArraySize;
        this.maxArraySize = maxArraySize;
    }

    /** Runs a sorting algorithm and builds its full response. */
    public VisualizationResponse sort(SortRequest request) {
        requireCategory(request.algorithm(), false);
        int[] array = toValidatedArray(request.array());

        VisualizationResult result = strategyFor(request.algorithm()).run(array, null);
        return toResponse(request.algorithm(), request.array(), result);
    }

    /** Runs a searching algorithm and builds its full response. */
    public VisualizationResponse search(SearchRequest request) {
        requireCategory(request.algorithm(), true);
        int[] array = toValidatedArray(request.array());
        if (request.target() == null) {
            throw new InvalidInputException("target is required for search");
        }
        if (request.algorithm() == AlgorithmType.BINARY_SEARCH) {
            requireSortedAscending(array);
        }

        VisualizationResult result =
                strategyFor(request.algorithm()).run(array, request.target());
        return toResponse(request.algorithm(), request.array(), result);
    }

    // === Validation ==========================================================

    private void requireCategory(AlgorithmType algorithm, boolean expectSearching) {
        if (algorithm.isSearching() != expectSearching) {
            String expected = expectSearching ? "a searching" : "a sorting";
            throw new InvalidInputException(
                    "%s is not %s algorithm — use the other endpoint"
                            .formatted(algorithm, expected));
        }
    }

    private int[] toValidatedArray(List<Integer> values) {
        if (values.size() < minArraySize || values.size() > maxArraySize) {
            throw new InvalidInputException(
                    "array size must be between %d and %d (was %d)"
                            .formatted(minArraySize, maxArraySize, values.size()));
        }
        int[] array = new int[values.size()];
        for (int i = 0; i < array.length; i++) {
            Integer value = values.get(i);
            if (value == null) {
                throw new InvalidInputException("array must not contain null values");
            }
            array[i] = value;
        }
        return array;
    }

    private void requireSortedAscending(int[] array) {
        for (int i = 1; i < array.length; i++) {
            if (array[i - 1] > array[i]) {
                throw new InvalidInputException(
                        "binary search requires a sorted (ascending) array");
            }
        }
    }

    private Algorithm strategyFor(AlgorithmType type) {
        return registry.find(type).orElseThrow(() ->
                new InvalidInputException("algorithm " + type + " is not available"));
    }

    // === Domain → DTO mapping ================================================

    private VisualizationResponse toResponse(
            AlgorithmType algorithm, List<Integer> initialArray, VisualizationResult result) {
        List<StepDto> steps = result.steps().stream()
                .map(VisualizationService::toDto)
                .toList();
        return new VisualizationResponse(
                algorithm,
                initialArray,
                result.totalSteps(),
                result.comparisons(),
                result.swaps(),
                result.found(),
                steps);
    }

    private static StepDto toDto(AlgorithmStep step) {
        return new StepDto(
                step.array(),
                emptyToNull(step.comparing()),
                emptyToNull(step.swapping()),
                emptyToNull(step.sorted()),
                step.pivot(),
                step.checking(),
                step.found(),
                step.low(),
                step.mid(),
                step.high(),
                step.description());
    }

    /** Lets the {@code @JsonInclude(NON_NULL)} on StepDto drop unused highlight arrays. */
    private static List<Integer> emptyToNull(List<Integer> list) {
        return (list == null || list.isEmpty()) ? null : list;
    }
}
