package org.gonn.gava;

import java.util.function.UnaryOperator;

// This is MUTABLE and NOT thread-safe.
public class Box<T> {
    private T t;

    public Box(T t) {this.t = t;}

    public static <T> Box<T> of(T t) {return new Box<>(t);}

    public T get() {return this.t;}

    public Box<T> set(T t) {
        this.t = t;
        return this;
    }

    public Box<T> modify(UnaryOperator<T> mod) {
        if (mod == null) throw new NullPointerException("given mod is null");
        this.t = mod.apply(this.t);
        return this;
    }

    // Returns if the value is null
    public boolean isEmpty() {return this.t == null;}

    public void clear() {this.t = null;}

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Box<?> other)) return false;
        if (this.t == null) return other.t == null;
        return this.t.equals(other.t);
    }

    @Override
    public int hashCode() {
        return this.t == null ? 0 : this.t.hashCode();
    }

    @Override
    public String toString() {
        return this.t == null ? "null" : this.t.toString();
    }
}
