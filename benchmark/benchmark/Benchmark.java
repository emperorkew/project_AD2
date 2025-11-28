package benchmark;

import oplossing.*;
import java.util.*;
import java.util.function.Consumer;

/**
 * Simple benchmark to compare tree implementations.
 */
public class Benchmark {

    private static final int WARMUP = 1000;
    private static final int OPERATIONS = 10000;
    private static final int RUNS = 5;

    // Tree implementations to benchmark
    private enum TreeType {
        BST("SearchTreeImplemented"),
        SPLAY("SemiSplayTree"),
        TREAP("Treap"),
        LINEAR_FREQ("LineairFrequencyTreap"),
        LOG_FREQ("MyFrequencyTreap"),
        TIME_BASED("MyTreap (time-based)");

        final String displayName;

        TreeType(String displayName) {
            this.displayName = displayName;
        }
    }

    static void main() {
        System.out.println("=== Tree Implementation Benchmark ===\n");
        System.out.println("Operations per test: " + OPERATIONS);
        System.out.println("Runs per test: " + RUNS + "\n");

        // Warmup JVM
        System.out.println("Warming up JVM...");
        warmup();
        System.out.println("Warmup complete.\n");

        // Run benchmarks
        benchmarkAdd();
        benchmarkSearch();
        benchmarkRemove();
        benchmarkMixed();
    }

    private static void warmup() {
        for (int i = 0; i < WARMUP; i++) {
            SearchTree<Integer> tree = new SearchTree<>();
            for (int j = 0; j < 100; j++) tree.add(j);
        }
    }

    /**
     * Creates a tree instance based on the type.
     */
    private static opgave.SearchTree<Integer> createTree(TreeType type) {
        return switch (type) {
            case BST -> new SearchTree<>();
            case SPLAY -> new SemiSplayTree<>();
            case TREAP -> new Treap<>();
            case LINEAR_FREQ -> new LineairFrequencyTreap<>();
            case LOG_FREQ -> new MyFrequencyTreap<>();
            case TIME_BASED -> new MyTreap<>();
        };
    }

    /**
     * Runs a benchmark for all tree types and prints results.
     */
    private static void runBenchmark(Consumer<opgave.SearchTree<Integer>> operation) {
        System.out.println("=== " + "ADD Benchmark (sequential)" + " ===");

        Map<TreeType, long[]> results = new EnumMap<>(TreeType.class);
        for (TreeType type : TreeType.values()) {
            results.put(type, new long[RUNS]);
        }

        for (int run = 0; run < RUNS; run++) {
            for (TreeType type : TreeType.values()) {
                opgave.SearchTree<Integer> tree = createTree(type);

                long start = System.nanoTime();
                operation.accept(tree);
                long elapsed = System.nanoTime() - start;

                results.get(type)[run] = elapsed;
            }
        }

        // Print results
        for (TreeType type : TreeType.values()) {
            printResults(type.displayName, results.get(type));
        }
        System.out.println();
    }

    private static void benchmarkAdd() {
        runBenchmark(tree -> {
            for (int i = 0; i < OPERATIONS; i++) {
                tree.add(i);
            }
        });
    }

    private static void benchmarkSearch() {
        System.out.println("=== SEARCH Benchmark (random) ===");

        Random rnd = new Random(42);
        int[] searchKeys = new int[OPERATIONS];
        for (int i = 0; i < OPERATIONS; i++) {
            searchKeys[i] = rnd.nextInt(OPERATIONS);
        }

        Map<TreeType, long[]> results = new EnumMap<>(TreeType.class);
        for (TreeType type : TreeType.values()) {
            results.put(type, new long[RUNS]);
        }

        for (int run = 0; run < RUNS; run++) {
            for (TreeType type : TreeType.values()) {
                opgave.SearchTree<Integer> tree = createTree(type);

                // Populate tree
                for (int i = 0; i < OPERATIONS; i++) {
                    tree.add(i);
                }

                // Benchmark searches
                long start = System.nanoTime();
                for (int key : searchKeys) {
                    tree.search(key);
                }
                long elapsed = System.nanoTime() - start;

                results.get(type)[run] = elapsed;
            }
        }

        // Print results
        for (TreeType type : TreeType.values()) {
            printResults(type.displayName, results.get(type));
        }
        System.out.println();
    }

    private static void benchmarkRemove() {
        System.out.println("=== REMOVE Benchmark (random) ===");

        Random rnd = new Random(42);
        int[] removeKeys = new int[OPERATIONS];
        for (int i = 0; i < OPERATIONS; i++) {
            removeKeys[i] = rnd.nextInt(OPERATIONS);
        }

        Map<TreeType, long[]> results = new EnumMap<>(TreeType.class);
        for (TreeType type : TreeType.values()) {
            results.put(type, new long[RUNS]);
        }

        for (int run = 0; run < RUNS; run++) {
            for (TreeType type : TreeType.values()) {
                opgave.SearchTree<Integer> tree = createTree(type);

                // Populate tree
                for (int i = 0; i < OPERATIONS; i++) {
                    tree.add(i);
                }

                // Benchmark removes
                long start = System.nanoTime();
                for (int key : removeKeys) {
                    tree.remove(key);
                }
                long elapsed = System.nanoTime() - start;

                results.get(type)[run] = elapsed;
            }
        }

        // Print results
        for (TreeType type : TreeType.values()) {
            printResults(type.displayName, results.get(type));
        }
        System.out.println();
    }

    private static void benchmarkMixed() {
        System.out.println("=== MIXED Benchmark (add/search/remove) ===");

        Map<TreeType, long[]> results = new EnumMap<>(TreeType.class);
        for (TreeType type : TreeType.values()) {
            results.put(type, new long[RUNS]);
        }

        for (int run = 0; run < RUNS; run++) {
            for (TreeType type : TreeType.values()) {
                Random rnd = new Random(42); // Reset for consistency across tree types
                opgave.SearchTree<Integer> tree = createTree(type);

                long start = System.nanoTime();
                for (int i = 0; i < OPERATIONS; i++) {
                    int op = rnd.nextInt(3);
                    int key = rnd.nextInt(OPERATIONS / 2);
                    if (op == 0) tree.add(key);
                    else if (op == 1) tree.search(key);
                    else tree.remove(key);
                }
                long elapsed = System.nanoTime() - start;

                results.get(type)[run] = elapsed;
            }
        }

        // Print results
        for (TreeType type : TreeType.values()) {
            printResults(type.displayName, results.get(type));
        }
        System.out.println();
    }

    private static void printResults(String name, long[] times) {
        Arrays.sort(times);
        long median = times[RUNS / 2];
        long min = times[0];
        long max = times[RUNS - 1];

        System.out.printf("  %-25s median: %8.2f ms  (min: %.2f, max: %.2f)%n",
                name, median / 1_000_000.0, min / 1_000_000.0, max / 1_000_000.0);
    }
}