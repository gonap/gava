package org.gonn.gava;

public class Pair<T, U> {
    private final T first;
    private final U second;

    public Pair(T first, U second) {
        this.first = first;
        this.second = second;
    }

    public T getFirst() {
        return this.first;
    }

    public U getSecond() {
        return this.second;
    }

    public static <T, U> Pair<T, U> of(T first, U second) {
        return new Pair<T, U>(first, second);
    }
}
