package com.harsh.visualizer.enums;

/**
 * The set of algorithms this engine can visualize, each carrying its
 * display name, category, and Big-O complexity metadata.
 *
 * <p>Holding the complexity data on the enum means a single source of truth:
 * the {@code GET /api/visualize/algorithms} endpoint serves it directly, so
 * the frontend's complexity cards never hardcode anything.</p>
 */
public enum AlgorithmType {

    // === Sorting ===
    BUBBLE_SORT("Bubble Sort", AlgorithmCategory.SORTING,
            "O(n)", "O(n^2)", "O(n^2)", "O(1)", true,
            "Repeatedly steps through the list, swapping adjacent elements that are out of order."),

    SELECTION_SORT("Selection Sort", AlgorithmCategory.SORTING,
            "O(n^2)", "O(n^2)", "O(n^2)", "O(1)", false,
            "Repeatedly selects the smallest remaining element and moves it to the sorted prefix."),

    INSERTION_SORT("Insertion Sort", AlgorithmCategory.SORTING,
            "O(n)", "O(n^2)", "O(n^2)", "O(1)", true,
            "Builds the sorted array one element at a time by inserting each into its correct place."),

    MERGE_SORT("Merge Sort", AlgorithmCategory.SORTING,
            "O(n log n)", "O(n log n)", "O(n log n)", "O(n)", true,
            "Divides the array in half, recursively sorts each half, then merges them."),

    QUICK_SORT("Quick Sort", AlgorithmCategory.SORTING,
            "O(n log n)", "O(n log n)", "O(n^2)", "O(log n)", false,
            "Partitions around a pivot so smaller elements come before it and larger after, then recurses."),

    // === Searching ===
    LINEAR_SEARCH("Linear Search", AlgorithmCategory.SEARCHING,
            "O(1)", "O(n)", "O(n)", "O(1)", false,
            "Checks each element from left to right until the target is found."),

    BINARY_SEARCH("Binary Search", AlgorithmCategory.SEARCHING,
            "O(1)", "O(log n)", "O(log n)", "O(1)", false,
            "Repeatedly halves a sorted range, comparing the middle element to the target.");

    private final String displayName;
    private final AlgorithmCategory category;
    private final String bestCase;
    private final String averageCase;
    private final String worstCase;
    private final String spaceComplexity;
    private final boolean stable;
    private final String description;

    AlgorithmType(String displayName, AlgorithmCategory category,
                  String bestCase, String averageCase, String worstCase,
                  String spaceComplexity, boolean stable, String description) {
        this.displayName = displayName;
        this.category = category;
        this.bestCase = bestCase;
        this.averageCase = averageCase;
        this.worstCase = worstCase;
        this.spaceComplexity = spaceComplexity;
        this.stable = stable;
        this.description = description;
    }

    public String getDisplayName() { return displayName; }
    public AlgorithmCategory getCategory() { return category; }
    public String getBestCase() { return bestCase; }
    public String getAverageCase() { return averageCase; }
    public String getWorstCase() { return worstCase; }
    public String getSpaceComplexity() { return spaceComplexity; }
    public boolean isStable() { return stable; }
    public String getDescription() { return description; }

    public boolean isSearching() {
        return category == AlgorithmCategory.SEARCHING;
    }
}
