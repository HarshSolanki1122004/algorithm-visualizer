package com.harsh.visualizer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * End-to-end tests over the real HTTP layer (B4.6): full Spring context, real
 * strategy auto-wiring, JSON (de)serialisation, validation, and the global
 * exception handler. Verifies the API contract the frontend depends on.
 */
@SpringBootTest
@AutoConfigureMockMvc
class VisualizationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String json(Object body) throws Exception {
        return objectMapper.writeValueAsString(body);
    }

    // === /sort ===============================================================

    @Test
    void sortReturnsFullTrace() throws Exception {
        String body = json(Map.of("algorithm", "BUBBLE_SORT", "array", List.of(12, 45, 3, 78, 21)));

        mockMvc.perform(post("/api/visualize/sort").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.algorithm", is("BUBBLE_SORT")))
                .andExpect(jsonPath("$.totalSteps", greaterThan(0)))
                .andExpect(jsonPath("$.comparisons", greaterThan(0)))
                .andExpect(jsonPath("$.steps", hasSize(greaterThan(0))))
                // The first recorded frame is a comparison over the original array.
                .andExpect(jsonPath("$.steps[0].array[0]", is(12)));
    }

    @Test
    void sortRejectsEmptyArray() throws Exception {
        String body = json(Map.of("algorithm", "BUBBLE_SORT", "array", List.of()));

        mockMvc.perform(post("/api/visualize/sort").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)));
    }

    @Test
    void sortRejectsOversizedArray() throws Exception {
        List<Integer> big = java.util.stream.IntStream.rangeClosed(1, 50).boxed().toList();
        String body = json(Map.of("algorithm", "BUBBLE_SORT", "array", big));

        mockMvc.perform(post("/api/visualize/sort").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("array size must be between 2 and 30 (was 50)")));
    }

    @Test
    void sortRejectsUnknownAlgorithm() throws Exception {
        String body = "{\"algorithm\":\"NOT_REAL\",\"array\":[1,2,3]}";

        mockMvc.perform(post("/api/visualize/sort").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void sortRejectsSearchAlgorithm() throws Exception {
        String body = json(Map.of("algorithm", "BINARY_SEARCH", "array", List.of(1, 2, 3)));

        mockMvc.perform(post("/api/visualize/sort").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }

    // === /search =============================================================

    @Test
    void binarySearchFindsTarget() throws Exception {
        String body = json(Map.of("algorithm", "BINARY_SEARCH",
                "array", List.of(3, 12, 21, 45, 78), "target", 45));

        mockMvc.perform(post("/api/visualize/search").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.algorithm", is("BINARY_SEARCH")))
                .andExpect(jsonPath("$.found", is(true)))
                .andExpect(jsonPath("$.steps[0].low", is(0)))
                .andExpect(jsonPath("$.steps[0].high", is(4)));
    }

    @Test
    void linearSearchReportsNotFound() throws Exception {
        String body = json(Map.of("algorithm", "LINEAR_SEARCH",
                "array", List.of(3, 12, 21), "target", 99));

        mockMvc.perform(post("/api/visualize/search").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.found", is(false)));
    }

    @Test
    void searchRequiresTarget() throws Exception {
        String body = json(Map.of("algorithm", "LINEAR_SEARCH", "array", List.of(3, 12, 21)));

        mockMvc.perform(post("/api/visualize/search").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.target").exists());
    }

    @Test
    void binarySearchRejectsUnsortedArray() throws Exception {
        String body = json(Map.of("algorithm", "BINARY_SEARCH",
                "array", List.of(5, 1, 3), "target", 3));

        mockMvc.perform(post("/api/visualize/search").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("binary search requires a sorted (ascending) array")));
    }

    // === /algorithms =========================================================

    @Test
    void listsAllSevenAlgorithms() throws Exception {
        mockMvc.perform(get("/api/visualize/algorithms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(7)))
                .andExpect(jsonPath("$[0].bestCase").exists())
                .andExpect(jsonPath("$[0].displayName").exists());
    }
}
