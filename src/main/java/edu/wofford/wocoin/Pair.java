package edu.wofford.wocoin;

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

    public FirstType getFirst() {
        return first;
    }

    public SecondType getSecond() {
        return second;
    }
}
