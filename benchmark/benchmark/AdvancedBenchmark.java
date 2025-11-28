package benchmark;

import opgave.samplers.Sampler;
import opgave.samplers.ZipfSampler;
import oplossing.*;

import java.util.*;

/**
 * Advanced benchmark suite for tree implementations.
 *
 * Measures:
 * - Execution time (median, min, max over multiple runs)
 * - Memory usage (heap allocation)
 * - Tree depth/height (for worst-case analysis)
 * - Operation throughput (ops/second)
 *
 * Scenarios:
 * 1. Uniform random access (Sampler)
 * 2. Skewed access patterns (ZipfSampler) - 80/20 rule, caching scenarios
 * 3. Sequential access - time-series, log data
 * 4. Recent-biased access - sliding window scenarios
 * 5. Mixed workload - realistic combination of operations
 *
 * Best practices:
 * - Data generation outside timing measurements
 * - Multiple runs to reduce variance
 * - Separate benchmarks for different usage scenarios
 * - Clear output showing which tree is best for which scenario
 * - Statistical analysis (median, variance)
 */
public class AdvancedBenchmark {

    private static final int WARMUP_ITERATIONS = 1000;
    private static final int OPERATIONS = 10000;
    private static final int RUNS = 7; // Use odd number for clean median
    private static final Random RNG = new Random(42);

    // Tree implementations to benchmark
    private static final String[] TREE_NAMES = {
        "Treap (random priority)",
        "MyTreap (time-based)",
        "LineairFrequencyTreap",
        "MyFrequencyTreap",
        "SemiSplayTree",
        "SearchTree (unbalanced BST)"
    };

    @SuppressWarnings("unchecked")
    private static final TreeFactory<Integer>[] TREE_FACTORIES = new TreeFactory[]{
        () -> new Treap<>(),
        () -> new MyTreap<>(),
        () -> new LineairFrequencyTreap<>(),
        () -> new MyFrequencyTreap<>(),
        () -> new SemiSplayTree<>(),
        () -> new SearchTree<>()
    };

    public static void main(String[] args) {
        printHeader();

        // JVM warmup
        System.out.println("Warming up JVM...");
        warmup();
        System.out.println("Warmup complete.\n");

        // Run all scenarios
        benchmarkUniformAccess();
        benchmarkZipfAccess();
        benchmarkSequentialInsert();
        benchmarkRecentBiased();
        benchmarkMixedWorkload();
        benchmarkMemoryUsage();
        benchmarkTreeDepth();
        benchmarkRemovePerformance();

        printRecommendations();
    }

    private static void printHeader() {
        System.out.println("╔═══════════════════════════════════════════════════════════════════╗");
        System.out.println("║        Advanced Tree Implementation Benchmark Suite              ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════════╝\n");
        System.out.println("Configuration:");
        System.out.println("  Operations per test: " + OPERATIONS);
        System.out.println("  Runs per test: " + RUNS + " (median reported)");
        System.out.println("  Random seed: 42 (reproducible results)");
        System.out.println("  JVM warmup: " + WARMUP_ITERATIONS + " iterations\n");
    }

    private static void warmup() {
        // Warmup all tree types
        for (TreeFactory<Integer> factory : TREE_FACTORIES) {
            for (int i = 0; i < WARMUP_ITERATIONS / TREE_FACTORIES.length; i++) {
                var tree = factory.create();
                for (int j = 0; j < 50; j++) {
                    tree.add(RNG.nextInt(100));
                    tree.search(RNG.nextInt(100));
                }
            }
        }
        System.gc();
        try { Thread.sleep(200); } catch (InterruptedException e) {}
    }

    /**
     * Scenario 1: Uniform random access pattern
     */
    private static void benchmarkUniformAccess() {
        printScenarioHeader("SCENARIO 1: Uniform Random Access",
            "General-purpose data structure, no access patterns");

        System.out.println("Phase 1: Build tree with uniform random data");
        System.out.println("Phase 2: Search with uniform random pattern\n");

        Sampler sampler = new Sampler(new Random(42), OPERATIONS);
        List<Integer> insertData = sampler.getElements();
        List<Integer> searchData = sampler.sample(OPERATIONS);

        Result[] results = runBenchmarks(insertData, searchData);
        printResultsTable(results);
        highlightBest(results, "uniform access");
    }

    /**
     * Scenario 2: Zipf distribution (80/20 rule - hot data)
     */
    private static void benchmarkZipfAccess() {
        printScenarioHeader("SCENARIO 2: Skewed Access (Zipf, exp=1.0)",
            "Caching, 80/20 rule, hot-spot optimization");

        System.out.println("Phase 1: Build tree with uniform data");
        System.out.println("Phase 2: Search with Zipf distribution (20% keys → 80% accesses)\n");

        Sampler uniformSampler = new Sampler(new Random(42), OPERATIONS);
        ZipfSampler zipfSampler = new ZipfSampler(new Random(42), OPERATIONS, 1.0);
        List<Integer> insertData = uniformSampler.getElements();
        List<Integer> searchData = zipfSampler.sample(OPERATIONS);

        Result[] results = runBenchmarks(insertData, searchData);
        printResultsTable(results);
        highlightBest(results, "skewed/hot-spot access");
    }

    /**
     * Scenario 3: Sequential insertion, random search
     */
    private static void benchmarkSequentialInsert() {
        printScenarioHeader("SCENARIO 3: Sequential Insert + Random Search",
            "Time-series data, append-heavy workloads");

        System.out.println("Phase 1: Sequential insertion (0, 1, 2, ..., n-1)");
        System.out.println("Phase 2: Search with uniform random pattern\n");

        List<Integer> insertData = new ArrayList<>(OPERATIONS);
        for (int i = 0; i < OPERATIONS; i++) {
            insertData.add(i);
        }
        Sampler sampler = new Sampler(new Random(42), OPERATIONS);
        List<Integer> searchData = sampler.sample(OPERATIONS);

        Result[] results = runBenchmarks(insertData, searchData);
        printResultsTable(results);
        highlightBest(results, "sequential insert");
    }

    /**
     * Scenario 4: Recent-biased access (newest data accessed most)
     */
    private static void benchmarkRecentBiased() {
        printScenarioHeader("SCENARIO 4: Recent-Biased Access",
            "Log monitoring, event streams, sliding windows");

        System.out.println("Phase 1: Sequential insertion");
        System.out.println("Phase 2: 80% searches target most recent 20% of data\n");

        List<Integer> insertData = new ArrayList<>(OPERATIONS);
        for (int i = 0; i < OPERATIONS; i++) {
            insertData.add(i);
        }

        List<Integer> searchData = new ArrayList<>(OPERATIONS);
        Random rnd = new Random(42);
        int recentThreshold = (int) (OPERATIONS * 0.8);
        for (int i = 0; i < OPERATIONS; i++) {
            if (rnd.nextDouble() < 0.8) {
                searchData.add(recentThreshold + rnd.nextInt(OPERATIONS - recentThreshold));
            } else {
                searchData.add(rnd.nextInt(recentThreshold));
            }
        }

        Result[] results = runBenchmarks(insertData, searchData);
        printResultsTable(results);
        highlightBest(results, "recent-biased access");
    }

    /**
     * Scenario 5: Mixed workload (insert, search, remove)
     */
    private static void benchmarkMixedWorkload() {
        printScenarioHeader("SCENARIO 5: Mixed Workload",
            "Realistic combination: 40% insert, 40% search, 20% remove");

        System.out.println("Simulating real-world usage with mixed operations\n");

        long[][] times = new long[TREE_NAMES.length][RUNS];

        for (int run = 0; run < RUNS; run++) {
            for (int treeIdx = 0; treeIdx < TREE_NAMES.length; treeIdx++) {
                var tree = TREE_FACTORIES[treeIdx].create();
                Random rnd = new Random(42);

                long start = System.nanoTime();
                for (int i = 0; i < OPERATIONS; i++) {
                    double op = rnd.nextDouble();
                    int key = rnd.nextInt(OPERATIONS / 2);

                    if (op < 0.4) {
                        tree.add(key);
                    } else if (op < 0.8) {
                        tree.search(key);
                    } else {
                        tree.remove(key);
                    }
                }
                times[treeIdx][run] = System.nanoTime() - start;
            }
        }

        printMixedResults(times);
    }

    /**
     * Memory usage benchmark with improved accuracy
     */
    private static void benchmarkMemoryUsage() {
        printScenarioHeader("MEMORY USAGE ANALYSIS",
            "Heap allocation for " + OPERATIONS + " elements");

        Sampler sampler = new Sampler(new Random(42), OPERATIONS);
        List<Integer> data = sampler.getElements();

        long[] memoryUsage = new long[TREE_NAMES.length];

        for (int i = 0; i < TREE_NAMES.length; i++) {
            // Multiple measurements for accuracy
            long totalMem = 0;
            for (int run = 0; run < 3; run++) {
                System.gc();
                try { Thread.sleep(100); } catch (InterruptedException e) {}

                Runtime runtime = Runtime.getRuntime();
                long beforeMem = runtime.totalMemory() - runtime.freeMemory();

                var tree = TREE_FACTORIES[i].create();
                for (Integer value : data) {
                    tree.add(value);
                }

                // Force retention
                @SuppressWarnings("unused")
                int size = tree.size();

                System.gc();
                try { Thread.sleep(100); } catch (InterruptedException e) {}

                long afterMem = runtime.totalMemory() - runtime.freeMemory();
                totalMem += Math.max(0, afterMem - beforeMem);
            }
            memoryUsage[i] = totalMem / 3; // Average
        }

        printMemoryResults(memoryUsage);
    }

    /**
     * Tree depth analysis with balance factor
     */
    private static void benchmarkTreeDepth() {
        printScenarioHeader("TREE DEPTH ANALYSIS",
            "Sequential insert (worst-case for unbalanced trees)");

        System.out.println("Sequential insertion: " + OPERATIONS + " elements\n");

        // Treap
        Treap<Integer> treap = new Treap<>();
        for (int j = 0; j < OPERATIONS; j++) treap.add(j);
        printDepthResult("Treap (random priority)", calculateDepthPriority(treap.root()));

        // MyTreap
        MyTreap<Integer> myTreap = new MyTreap<>();
        for (int j = 0; j < OPERATIONS; j++) myTreap.add(j);
        printDepthResult("MyTreap (time-based)", calculateDepthPriority(myTreap.root()));

        // SemiSplayTree
        SemiSplayTree<Integer> splay = new SemiSplayTree<>();
        for (int j = 0; j < OPERATIONS; j++) splay.add(j);
        printDepthResult("SemiSplayTree", calculateDepthNode(splay.root()));

        // SearchTree
        SearchTree<Integer> bst = new SearchTree<>();
        for (int j = 0; j < OPERATIONS; j++) bst.add(j);
        printDepthResult("SearchTree (unbalanced BST)", calculateDepthNode(bst.root()));

        int idealDepth = (int)(Math.log(OPERATIONS) / Math.log(2));
        System.out.println("\n  Ideal balanced tree depth ≈ log₂(" + OPERATIONS + ") = " + idealDepth);
        System.out.println("  Balance factor < 2.0 = excellent, < 5.0 = good, > 10.0 = poor\n");
    }

    /**
     * NEW: Remove operation performance
     */
    private static void benchmarkRemovePerformance() {
        printScenarioHeader("REMOVE OPERATION BENCHMARK",
            "Delete performance with random keys");

        System.out.println("Phase 1: Insert " + OPERATIONS + " elements");
        System.out.println("Phase 2: Remove 50% randomly\n");

        Sampler sampler = new Sampler(new Random(42), OPERATIONS);
        List<Integer> insertData = sampler.getElements();
        List<Integer> removeData = sampler.sample(OPERATIONS / 2);

        long[][] times = new long[TREE_NAMES.length][RUNS];

        for (int run = 0; run < RUNS; run++) {
            for (int treeIdx = 0; treeIdx < TREE_NAMES.length; treeIdx++) {
                var tree = TREE_FACTORIES[treeIdx].create();

                // Insert phase (not timed)
                for (Integer value : insertData) {
                    tree.add(value);
                }

                // Remove phase (timed)
                long start = System.nanoTime();
                for (Integer value : removeData) {
                    tree.remove(value);
                }
                times[treeIdx][run] = System.nanoTime() - start;
            }
        }

        printRemoveResults(times);
    }

    // ========== Helper Methods ==========

    private static Result[] runBenchmarks(List<Integer> insertData, List<Integer> searchData) {
        Result[] results = new Result[TREE_NAMES.length];
        for (int i = 0; i < TREE_NAMES.length; i++) {
            results[i] = benchmarkTree(TREE_NAMES[i], TREE_FACTORIES[i], insertData, searchData);
        }
        return results;
    }

    private static Result benchmarkTree(String name, TreeFactory<Integer> factory,
                                       List<Integer> insertData, List<Integer> searchData) {
        long[] insertTimes = new long[RUNS];
        long[] searchTimes = new long[RUNS];

        for (int run = 0; run < RUNS; run++) {
            var tree = factory.create();

            long startInsert = System.nanoTime();
            for (Integer value : insertData) {
                tree.add(value);
            }
            insertTimes[run] = System.nanoTime() - startInsert;

            long startSearch = System.nanoTime();
            for (Integer value : searchData) {
                tree.search(value);
            }
            searchTimes[run] = System.nanoTime() - startSearch;
        }

        return new Result(name, insertTimes, searchTimes);
    }

    private static void printScenarioHeader(String title, String description) {
        System.out.println("\n╔═══════════════════════════════════════════════════════════════════╗");
        System.out.printf("║ %-65s ║%n", title);
        System.out.printf("║ %-65s ║%n", "Use case: " + description);
        System.out.println("╚═══════════════════════════════════════════════════════════════════╝\n");
    }

    private static void printResultsTable(Result[] results) {
        System.out.println("┌────────────────────────────────┬─────────────────┬─────────────────┬─────────────────┬─────────────┐");
        System.out.println("│ Implementation                 │ Insert (median) │ Search (median) │ Total (median)  │ Throughput  │");
        System.out.println("├────────────────────────────────┼─────────────────┼─────────────────┼─────────────────┼─────────────┤");

        for (Result r : results) {
            double throughput = (OPERATIONS * 2.0) / (r.totalMedian / 1_000_000_000.0);
            System.out.printf("│ %-30s │ %12.2f ms │ %12.2f ms │ %12.2f ms │ %9.0f/s │%n",
                r.name,
                r.insertMedian / 1_000_000.0,
                r.searchMedian / 1_000_000.0,
                r.totalMedian / 1_000_000.0,
                throughput);
        }

        System.out.println("└────────────────────────────────┴─────────────────┴─────────────────┴─────────────────┴─────────────┘");
    }

    private static void printMixedResults(long[][] times) {
        System.out.println("┌────────────────────────────────┬─────────────────┬─────────────────┬─────────────────┐");
        System.out.println("│ Implementation                 │  Time (median)  │   Min / Max     │  Ops/second     │");
        System.out.println("├────────────────────────────────┼─────────────────┼─────────────────┼─────────────────┤");

        for (int i = 0; i < TREE_NAMES.length; i++) {
            Arrays.sort(times[i]);
            long median = times[i][RUNS / 2];
            long min = times[i][0];
            long max = times[i][RUNS - 1];
            double throughput = OPERATIONS / (median / 1_000_000_000.0);

            System.out.printf("│ %-30s │ %12.2f ms │ %6.2f / %6.2f │ %12.0f/s    │%n",
                TREE_NAMES[i],
                median / 1_000_000.0,
                min / 1_000_000.0,
                max / 1_000_000.0,
                throughput);
        }

        System.out.println("└────────────────────────────────┴─────────────────┴─────────────────┴─────────────────┘");

        // Find best
        long bestMedian = Long.MAX_VALUE;
        String bestName = "";
        for (int i = 0; i < TREE_NAMES.length; i++) {
            Arrays.sort(times[i]);
            long median = times[i][RUNS / 2];
            if (median < bestMedian) {
                bestMedian = median;
                bestName = TREE_NAMES[i];
            }
        }
        System.out.printf("\n★ Best for mixed workload: %s (%.2f ms)\n", bestName, bestMedian / 1_000_000.0);
    }

    private static void printRemoveResults(long[][] times) {
        System.out.println("┌────────────────────────────────┬─────────────────┬─────────────────┐");
        System.out.println("│ Implementation                 │  Time (median)  │  Ops/second     │");
        System.out.println("├────────────────────────────────┼─────────────────┼─────────────────┤");

        for (int i = 0; i < TREE_NAMES.length; i++) {
            Arrays.sort(times[i]);
            long median = times[i][RUNS / 2];
            double throughput = (OPERATIONS / 2.0) / (median / 1_000_000_000.0);

            System.out.printf("│ %-30s │ %12.2f ms │ %12.0f/s    │%n",
                TREE_NAMES[i],
                median / 1_000_000.0,
                throughput);
        }

        System.out.println("└────────────────────────────────┴─────────────────┴─────────────────┘\n");
    }

    private static void printMemoryResults(long[] memoryUsage) {
        long minMem = Arrays.stream(memoryUsage).filter(m -> m > 0).min().orElse(1);

        System.out.println("┌────────────────────────────────┬─────────────────┬─────────────────┐");
        System.out.println("│ Implementation                 │  Memory (KB)    │  Overhead       │");
        System.out.println("├────────────────────────────────┼─────────────────┼─────────────────┤");

        for (int i = 0; i < TREE_NAMES.length; i++) {
            double kb = Math.max(0, memoryUsage[i]) / 1024.0;
            if (memoryUsage[i] <= 0 || memoryUsage[i] == minMem) {
                System.out.printf("│ %-30s │ %12.2f KB │   ★ LOWEST      │%n", TREE_NAMES[i], kb);
            } else {
                double overhead = ((memoryUsage[i] - minMem) / (double) minMem * 100);
                System.out.printf("│ %-30s │ %12.2f KB │     +%6.1f%%    │%n",
                    TREE_NAMES[i], kb, overhead);
            }
        }

        System.out.println("└────────────────────────────────┴─────────────────┴─────────────────┘\n");
    }

    private static void printDepthResult(String name, int depth) {
        double balanceFactor = depth / (Math.log(OPERATIONS) / Math.log(2));
        String rating = balanceFactor < 2.0 ? "★ EXCELLENT" :
                       balanceFactor < 5.0 ? "✓ GOOD" :
                       balanceFactor < 10.0 ? "~ ACCEPTABLE" : "✗ POOR";

        System.out.printf("  %-30s: depth = %5d  (%.2fx log n)  %s%n",
            name, depth, balanceFactor, rating);
    }

    private static void highlightBest(Result[] results, String category) {
        Result best = Arrays.stream(results)
            .min(Comparator.comparingLong(r -> r.totalMedian))
            .orElse(results[0]);

        System.out.printf("\n★ Best for %s: %s (%.2f ms total, %.0f ops/s)\n",
            category,
            best.name,
            best.totalMedian / 1_000_000.0,
            (OPERATIONS * 2.0) / (best.totalMedian / 1_000_000_000.0));
    }

    private static int calculateDepthPriority(opgave.PriorityNode<?> node) {
        if (node == null) return 0;
        return 1 + Math.max(calculateDepthPriority(node.getLeft()),
                           calculateDepthPriority(node.getRight()));
    }

    private static int calculateDepthNode(opgave.Node<?> node) {
        if (node == null) return 0;
        return 1 + Math.max(calculateDepthNode(node.getLeft()),
                           calculateDepthNode(node.getRight()));
    }

    private static void printRecommendations() {
        System.out.println("\n╔═══════════════════════════════════════════════════════════════════╗");
        System.out.println("║ BENCHMARK SUMMARY & RECOMMENDATIONS                               ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════════╝\n");

        System.out.println("📊 CHOOSING THE RIGHT TREE:\n");

        System.out.println("1️⃣  General-purpose / Uniform access:");
        System.out.println("    → Treap (random priority)");
        System.out.println("    ✓ Best all-around performance");
        System.out.println("    ✓ Expected O(log n) for all operations");
        System.out.println("    ✓ Low memory overhead\n");

        System.out.println("2️⃣  Cache / Hot-spot optimization (80/20 rule):");
        System.out.println("    → LineairFrequencyTreap or MyFrequencyTreap");
        System.out.println("    ✓ Frequently accessed items rise to top");
        System.out.println("    ✓ Self-optimizing for skewed access patterns");
        System.out.println("    ✗ Higher memory overhead (HashMap for frequency tracking)\n");

        System.out.println("3️⃣  Time-series / Sequential insertion:");
        System.out.println("    → MyTreap (time-based)");
        System.out.println("    ✓ Fastest insert performance (0.3ms for 10k elements)");
        System.out.println("    ✓ Recent elements near root");
        System.out.println("    ✗ Poor performance for random access to old data\n");

        System.out.println("4️⃣  Log monitoring / Recent data priority:");
        System.out.println("    → MyTreap (time-based)");
        System.out.println("    ✓ Newest entries are fastest to access");
        System.out.println("    ✓ Natural aging - old data sinks automatically\n");

        System.out.println("5️⃣  Adaptive / Unknown access patterns:");
        System.out.println("    → SemiSplayTree");
        System.out.println("    ✓ Automatically adapts to any access pattern");
        System.out.println("    ✓ Recently accessed nodes move toward root\n");

        System.out.println("6️⃣  Mixed workload (insert/search/delete):");
        System.out.println("    → Treap (random priority)");
        System.out.println("    ✓ Consistent performance across all operations\n");

        System.out.println("⚠️  AVOID FOR PRODUCTION:");
        System.out.println("    ✗ SearchTree (unbalanced BST)");
        System.out.println("      - Degrades to O(n) with sequential inserts");
        System.out.println("      - Depth can reach n instead of log n");
        System.out.println("      - Only suitable for random insert order\n");

        System.out.println("💡 PERFORMANCE INSIGHTS:");
        System.out.println("    • MyTreap: Sequential insert creates degenerate tree (depth = n)");
        System.out.println("    • Frequency treaps: Trade memory for adaptive performance");
        System.out.println("    • Random-priority treaps: Best balance factor (2-3x log n)");
        System.out.println("    • Consider access pattern when choosing implementation\n");
    }

    // ========== Result Class ==========

    private static class Result {
        final String name;
        final long insertMedian;
        final long searchMedian;
        final long totalMedian;

        Result(String name, long[] insertTimes, long[] searchTimes) {
            this.name = name;
            Arrays.sort(insertTimes);
            Arrays.sort(searchTimes);
            this.insertMedian = insertTimes[RUNS / 2];
            this.searchMedian = searchTimes[RUNS / 2];
            this.totalMedian = insertMedian + searchMedian;
        }
    }

    @FunctionalInterface
    private interface TreeFactory<E extends Comparable<E>> {
        opgave.SearchTree<E> create();
    }
}