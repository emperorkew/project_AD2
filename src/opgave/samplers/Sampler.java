package opgave.samplers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * A generator of uniformly distributed samples.
 *
 * Usage:
 * 1. Construct a sampler
 * 2. (Optional) Add elements to yo@ur collection using getElements()
 * 3. Collect samples
 * 4. Benchmark using the samples
 */
public class Sampler {
    /** Source of uniform randomness */
    protected final Random rng;
    /** Number of elements to sample from */
    protected int numberOfElements;
    /** Elements to sample from */
    protected List<Integer> elements;

    public Sampler(Random rng, int numberOfElements) {
        this.rng = rng;
        if (numberOfElements <= 0) {
            throw new IllegalArgumentException("number of elements is not strictly positive: " + numberOfElements);
        }
        this.numberOfElements = numberOfElements;
        this.elements = new ArrayList<>(numberOfElements);
        for (int i = 0; i < numberOfElements; i++) {
            this.elements.add(i);
        }
        Collections.shuffle(this.elements, rng);
    }

    /**
     * Internal method which should return the index of the sample
     *
     * @return an integer with a probability according to the distribution.
     */
    protected int sample() {
        return rng.nextInt(this.numberOfElements);
    }

    /**
     * Creates a list of samples by sampling the elements in this class
     * according to the distribution represented by this class.
     *
     * @param numSamples how many samples should be taken
     * @return a list of the given length with samples
     */
    public List<Integer> sample(int numSamples) {
        List<Integer> shuffled = new ArrayList<>(this.elements);
        Collections.shuffle(shuffled, rng);
        List<Integer> samples = new ArrayList<>(numSamples);
        for (int i = 0; i < numSamples; i++) {
            samples.add(shuffled.get(this.sample()));
        }
        return samples;
    }

    /**
     * @return the list of elements to sample from
     */
    public List<Integer> getElements() {
        return elements;
    }
}
