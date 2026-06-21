package com.harsh.visualizer.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * A single frame in the visualization trace. The frontend reads these fields
 * to color the numbered boxes for this step.
 *
 * <p>Null/empty fields are omitted from JSON to keep the payload lean — a sort
 * step won't carry search fields and vice versa.</p>
 *
 * @param array     snapshot of the array at this step
 * @param comparing indices currently being compared (highlight: amber)
 * @param swapping  indices being swapped this step (highlight: orange)
 * @param sorted    indices now in their final sorted position (highlight: green)
 * @param pivot     pivot index for quicksort, if any (highlight: purple)
 * @param checking  index being inspected during a search (highlight: amber)
 * @param found     index where the search target was found (highlight: teal)
 * @param low       binary search: lower bound of the active window
 * @param mid       binary search: midpoint being compared
 * @param high      binary search: upper bound of the active window
 * @param description human-readable explanation of this step
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record StepDto(
        List<Integer> array,
        List<Integer> comparing,
        List<Integer> swapping,
        List<Integer> sorted,
        Integer pivot,
        Integer checking,
        Integer found,
        Integer low,
        Integer mid,
        Integer high,
        String description
) {
}
