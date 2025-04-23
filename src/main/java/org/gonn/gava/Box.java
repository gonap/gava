package org.gonn.gava;

import java.util.function.UnaryOperator;

public class Box<T> {
    private T t;

    public static <T> Box<T> of(T t) {
        return new Box<>(t);
    }

    public Box(T t) { this.t = t; }

    public T get() { return this.t; } 

    public Box<T> set(T t) {
        this.t = t;
        return this;
    }

    public Box<T> modify(UnaryOperator<T> mod) {
        this.t = mod.apply(this.t);
        return this;
    }

    public boolean isEmpty() { return this.t == null; }

    public void clear() { this.t = null; }

    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj instanceof Box) {
            Box<?> other = (Box<?>) obj;
            return this.t.equals(other.t);
        }
        return false;
    }

    public int hashCode() {
        return this.t == null ? 0 : this.t.hashCode();
    }

    public String toString() {
        return this.t == null ? "null" : this.t.toString();
    }
}
