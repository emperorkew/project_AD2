package test;

import org.junit.jupiter.api.Nested;

/**
 * Main test class that runs all tests.
 * Contains nested test classes for different tree implementations.
 */
public class test {

    @Nested
    class SearchTreeTests extends SearchTreeImplementedTest {
        // All tests from SearchTreeImplementedTest
    }

    @Nested
    class SemiSplayTests extends SemiSplayTreeTest {
        // All tests from SemiSplayTreeTest
    }
}