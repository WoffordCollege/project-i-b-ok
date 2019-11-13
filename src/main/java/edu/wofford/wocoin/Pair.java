package edu.wofford.wocoin;

/**
 * This Class is an Object similar to a tuple.
 * It is immutable and must be recreated to change the values stored inside
 * @param <FirstType> The type of Object stored in the first element
 * @param <SecondType> The type of Object stored in the second element
 */
public class Pair<FirstType, SecondType> {

    private FirstType first;
    private SecondType second;

    /**
     * Creates a pair
     *
     * @param first  the first element
     * @param second the second element
     */
    public Pair(FirstType first, SecondType second) {
        this.first = first;
        this.second = second;
    }

    /**
     * This function gets the value stored in element 1
     * @return the value stored in element one with type FirstType
     */
    public FirstType getFirst() {
        return first;
    }

    /**
     * This function gets the value stored in element 2
     * @return the value stored in element one with type SecondType
     */
    public SecondType getSecond() {
        return second;
    }
}
