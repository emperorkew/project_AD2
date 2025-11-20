package test;

import org.junit.jupiter.api.Nested;

/**
 * Main test class that runs all tests.
 * Contains nested test classes for different tree implementations.
 */
public class Test {

    @Nested
    class SearchTreeTests extends SearchTreeImplementedTest {
        // All tests from SearchTreeImplementedTest
    }

    @Nested
    class SemiSplayTests extends SemiSplayTreeTest {
        // All tests from SemiSplayTreeTest
    }

    @Nested
    class TreapTests extends TreapTest {
        // All tests from TreapTest
    }

    @Nested
    class LineairFreqnuencyTreapTests extends LineairFrequencyTreapTest {
        // All tests from LineairFrequencyTreapTest
    }

    @Nested
    class FrequencyTreapTests extends FrequencyTreapTest {
        // All tests from FrequencyTreapTest
    }
}