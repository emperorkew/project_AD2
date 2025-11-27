package benchmark;

import oplossing.*;
import java.util.*;

/**
 * Simple benchmark to compare tree implementations.
 * Run with: java -cp out test.Benchmark
 */
public class Benchmark {

    private static final int WARMUP = 1000;
    private static final int OPERATIONS = 10000;
    private static final int RUNS = 5;

    public static void main(String[] args) {
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

    private static void benchmarkAdd() {
        System.out.println("=== ADD Benchmark (sequential) ===");

        long[] bstTimes = new long[RUNS];
        long[] splayTimes = new long[RUNS];
        long[] treapTimes = new long[RUNS];
        long[] freqTimes = new long[RUNS];

        for (int run = 0; run < RUNS; run++) {
            // SearchTreeImplemented
            SearchTree<Integer> bst = new SearchTree<>();
            long start = System.nanoTime();
            for (int i = 0; i < OPERATIONS; i++) bst.add(i);
            bstTimes[run] = System.nanoTime() - start;

            // SemiSplayTree
            SemiSplayTree<Integer> splay = new SemiSplayTree<>();
            start = System.nanoTime();
            for (int i = 0; i < OPERATIONS; i++) splay.add(i);
            splayTimes[run] = System.nanoTime() - start;

            // Treap
            Treap<Integer> treap = new Treap<>();
            start = System.nanoTime();
            for (int i = 0; i < OPERATIONS; i++) treap.add(i);
            treapTimes[run] = System.nanoTime() - start;

            // LineairFrequencyTreap
            LineairFrequencyTreap<Integer> freq = new LineairFrequencyTreap<>();
            start = System.nanoTime();
            for (int i = 0; i < OPERATIONS; i++) freq.add(i);
            freqTimes[run] = System.nanoTime() - start;
        }

        printResults("SearchTreeImplemented", bstTimes);
        printResults("SemiSplayTree", splayTimes);
        printResults("Treap", treapTimes);
        printResults("LineairFrequencyTreap", freqTimes);
        System.out.println();
    }

    private static void benchmarkSearch() {
        System.out.println("=== SEARCH Benchmark (random) ===");

        Random rnd = new Random(42);
        int[] searchKeys = new int[OPERATIONS];
        for (int i = 0; i < OPERATIONS; i++) searchKeys[i] = rnd.nextInt(OPERATIONS);

        long[] bstTimes = new long[RUNS];
        long[] splayTimes = new long[RUNS];
        long[] treapTimes = new long[RUNS];
        long[] freqTimes = new long[RUNS];

        for (int run = 0; run < RUNS; run++) {
            // Setup trees
            SearchTree<Integer> bst = new SearchTree<>();
            SemiSplayTree<Integer> splay = new SemiSplayTree<>();
            Treap<Integer> treap = new Treap<>();
            LineairFrequencyTreap<Integer> freq = new LineairFrequencyTreap<>();

            for (int i = 0; i < OPERATIONS; i++) {
                bst.add(i);
                splay.add(i);
                treap.add(i);
                freq.add(i);
            }

            // Benchmark searches
            long start = System.nanoTime();
            for (int key : searchKeys) bst.search(key);
            bstTimes[run] = System.nanoTime() - start;

            start = System.nanoTime();
            for (int key : searchKeys) splay.search(key);
            splayTimes[run] = System.nanoTime() - start;

            start = System.nanoTime();
            for (int key : searchKeys) treap.search(key);
            treapTimes[run] = System.nanoTime() - start;

            start = System.nanoTime();
            for (int key : searchKeys) freq.search(key);
            freqTimes[run] = System.nanoTime() - start;
        }

        printResults("SearchTreeImplemented", bstTimes);
        printResults("SemiSplayTree", splayTimes);
        printResults("Treap", treapTimes);
        printResults("LineairFrequencyTreap", freqTimes);
        System.out.println();
    }

    private static void benchmarkRemove() {
        System.out.println("=== REMOVE Benchmark (random) ===");

        Random rnd = new Random(42);
        int[] removeKeys = new int[OPERATIONS];
        for (int i = 0; i < OPERATIONS; i++) removeKeys[i] = rnd.nextInt(OPERATIONS);

        long[] bstTimes = new long[RUNS];
        long[] splayTimes = new long[RUNS];
        long[] treapTimes = new long[RUNS];
        long[] freqTimes = new long[RUNS];

        for (int run = 0; run < RUNS; run++) {
            // Setup trees
            SearchTree<Integer> bst = new SearchTree<>();
            SemiSplayTree<Integer> splay = new SemiSplayTree<>();
            Treap<Integer> treap = new Treap<>();
            LineairFrequencyTreap<Integer> freq = new LineairFrequencyTreap<>();

            for (int i = 0; i < OPERATIONS; i++) {
                bst.add(i);
                splay.add(i);
                treap.add(i);
                freq.add(i);
            }

            // Benchmark removes
            long start = System.nanoTime();
            for (int key : removeKeys) bst.remove(key);
            bstTimes[run] = System.nanoTime() - start;

            start = System.nanoTime();
            for (int key : removeKeys) splay.remove(key);
            splayTimes[run] = System.nanoTime() - start;

            start = System.nanoTime();
            for (int key : removeKeys) treap.remove(key);
            treapTimes[run] = System.nanoTime() - start;

            start = System.nanoTime();
            for (int key : removeKeys) freq.remove(key);
            freqTimes[run] = System.nanoTime() - start;
        }

        printResults("SearchTreeImplemented", bstTimes);
        printResults("SemiSplayTree", splayTimes);
        printResults("Treap", treapTimes);
        printResults("LineairFrequencyTreap", freqTimes);
        System.out.println();
    }

    private static void benchmarkMixed() {
        System.out.println("=== MIXED Benchmark (add/search/remove) ===");

        Random rnd = new Random(42);

        long[] bstTimes = new long[RUNS];
        long[] splayTimes = new long[RUNS];
        long[] treapTimes = new long[RUNS];
        long[] freqTimes = new long[RUNS];

        for (int run = 0; run < RUNS; run++) {
            rnd = new Random(42); // Reset for consistency

            // SearchTreeImplemented
            SearchTree<Integer> bst = new SearchTree<>();
            long start = System.nanoTime();
            for (int i = 0; i < OPERATIONS; i++) {
                int op = rnd.nextInt(3);
                int key = rnd.nextInt(OPERATIONS / 2);
                if (op == 0) bst.add(key);
                else if (op == 1) bst.search(key);
                else bst.remove(key);
            }
            bstTimes[run] = System.nanoTime() - start;

            rnd = new Random(42);

            // SemiSplayTree
            SemiSplayTree<Integer> splay = new SemiSplayTree<>();
            start = System.nanoTime();
            for (int i = 0; i < OPERATIONS; i++) {
                int op = rnd.nextInt(3);
                int key = rnd.nextInt(OPERATIONS / 2);
                if (op == 0) splay.add(key);
                else if (op == 1) splay.search(key);
                else splay.remove(key);
            }
            splayTimes[run] = System.nanoTime() - start;

            rnd = new Random(42);

            // Treap
            Treap<Integer> treap = new Treap<>();
            start = System.nanoTime();
            for (int i = 0; i < OPERATIONS; i++) {
                int op = rnd.nextInt(3);
                int key = rnd.nextInt(OPERATIONS / 2);
                if (op == 0) treap.add(key);
                else if (op == 1) treap.search(key);
                else treap.remove(key);
            }
            treapTimes[run] = System.nanoTime() - start;

            rnd = new Random(42);

            // LineairFrequencyTreap
            LineairFrequencyTreap<Integer> freq = new LineairFrequencyTreap<>();
            start = System.nanoTime();
            for (int i = 0; i < OPERATIONS; i++) {
                int op = rnd.nextInt(3);
                int key = rnd.nextInt(OPERATIONS / 2);
                if (op == 0) freq.add(key);
                else if (op == 1) freq.search(key);
                else freq.remove(key);
            }
            freqTimes[run] = System.nanoTime() - start;
        }

        printResults("SearchTreeImplemented", bstTimes);
        printResults("SemiSplayTree", splayTimes);
        printResults("Treap", treapTimes);
        printResults("LineairFrequencyTreap", freqTimes);
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