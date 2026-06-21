package com.harsh.visualizer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Algorithm Visualizer backend.
 *
 * <p>This service is the algorithm engine: for a given input array it runs the
 * requested sorting or searching algorithm in Java and returns the complete
 * step-by-step trace (every comparison, swap, and pivot) as JSON. The React
 * frontend is only a playback screen and contains no algorithm logic.</p>
 */
@SpringBootApplication
public class AlgorithmVisualizerApplication {

    public static void main(String[] args) {
        SpringApplication.run(AlgorithmVisualizerApplication.class, args);
    }
}
