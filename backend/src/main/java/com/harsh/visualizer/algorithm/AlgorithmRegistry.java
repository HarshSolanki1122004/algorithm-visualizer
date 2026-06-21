package com.harsh.visualizer.algorithm;

import com.harsh.visualizer.enums.AlgorithmType;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * The strategy registry: a single {@code Map<AlgorithmType, Algorithm>} keyed by
 * algorithm type (B3.6).
 *
 * <p>Spring injects every {@link Algorithm} bean it discovers (each sort and, from
 * Phase B4, each search) into the constructor; the registry indexes them by their
 * declared {@link Algorithm#type()}. The service layer then looks up the right
 * strategy for a request without any {@code switch} on the algorithm type — adding
 * a new algorithm is just dropping in a new {@code @Component}.</p>
 */
@Component
public class AlgorithmRegistry {

    private final Map<AlgorithmType, Algorithm> byType;

    public AlgorithmRegistry(List<Algorithm> algorithms) {
        Map<AlgorithmType, Algorithm> map = new EnumMap<>(AlgorithmType.class);
        for (Algorithm algorithm : algorithms) {
            Algorithm previous = map.put(algorithm.type(), algorithm);
            if (previous != null) {
                throw new IllegalStateException(
                        "Two algorithms registered for type " + algorithm.type()
                                + ": " + previous.getClass().getSimpleName()
                                + " and " + algorithm.getClass().getSimpleName());
            }
        }
        this.byType = Collections.unmodifiableMap(map);
    }

    /** Looks up the strategy for a type, if one is registered. */
    public Optional<Algorithm> find(AlgorithmType type) {
        return Optional.ofNullable(byType.get(type));
    }

    /**
     * Returns the strategy for a type or throws if none is registered.
     *
     * @throws IllegalArgumentException if no algorithm is registered for {@code type}
     */
    public Algorithm get(AlgorithmType type) {
        return find(type).orElseThrow(() -> new IllegalArgumentException(
                "No algorithm registered for type " + type));
    }

    /** Immutable view of the full registry. */
    public Map<AlgorithmType, Algorithm> asMap() {
        return byType;
    }
}
