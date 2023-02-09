package org.gonn.gava;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**
 * Box an object just like the java.util.Optional
 *
 * @param <T>
 * @author Gon Yi
 * @version 0.0.1
 * @link gonn.org
 * @created 2/8/2023
 * @see java.util.Optional
 */
public class Box<T> {
    private T value;

    public Box(T t) {
        this.value = t;
    }

    /**
     * Static constructor.
     * <code>
     * Box.of("1123").map(v->v.length()).get()
     * </code>
     *
     * @param obj
     * @param <T>
     * @return Box of type T.
     */
    public static <T> Box<T> of(T obj) {
        return new Box<>(obj);
    }

    /**
     * If the box's underlying value is NULL.
     *
     * @return true if value is NULL.
     */
    public boolean isEmpty() {
        return this.value == null;
    }

    /**
     * If the box's underlying value is NOT NULL.
     *
     * @return true if value is NOT NULL.
     */
    public boolean isFull() {
        return this.value != null;
    }

    /**
     * Get underlying value of the box. When the box is empty, this will return null.
     *
     * @return
     */
    public T get() {
        return this.value;
    }

    /**
     * Get underlying value of the box. However, if the box is empty, returns fallback of T.
     *
     * @param fallback
     * @return content of T.
     */
    public T get(T fallback) {
        return this.value == null ? fallback : value;
    }

    /**
     * Run consumer mod. Any modification should be done within the consumer.
     * Box's value will not be changed. Use "update" method to change Box's value.
     *
     * @param valueConsumer
     * @return
     */
    public Box<T> then(Consumer<T> valueConsumer) {
        if (valueConsumer != null && this.value != null)
            valueConsumer.accept(this.value);
        return this;
    }

    /**
     * Update will replace underlying T value.
     * Box's value will be changed within same value type.
     * Use "map" method in order to change to a different type.
     *
     * @param valueOperator
     * @return
     */
    public Box<T> update(UnaryOperator<T> valueOperator) {
        if (valueOperator != null && this.value != null)
            this.value = valueOperator.apply(this.value);
        return this;
    }

    /**
     * If not exists, function mod's value will be set for the value.
     * <code>
     * Box.of(new StringBuilder()).with(sb->sb.append("Hello")).map(sb->sb.size()).get()
     * </code>
     *
     * @param fallbackSupplier
     * @return self
     */
    public Box<T> or(Supplier<T> fallbackSupplier) {
        if (fallbackSupplier != null && this.value == null)
            this.value = fallbackSupplier.get();
        return this;
    }

    /**
     * If the Box is empty, throw an exception.
     *
     * @param exceptionSupplier
     * @param <X>
     * @return self
     * @throws X extends Throwable
     */
    public <X extends Throwable> Box<T> orThrow(Supplier<? extends X> exceptionSupplier) throws X {
        if (value == null) throw exceptionSupplier.get();
        return this;
    }

    /**
     * Map current underlying value of T into value of Box of R.
     *
     * @param valueFunction modification function
     * @param <R>           new type
     * @return new boxed R.
     */
    public <R> Box<R> map(Function<T, R> valueFunction) {
        return new Box<>(valueFunction.apply(this.value));
    }

    /**
     * When valueFunction failed during map(), use output from fallbackSupplier()
     *
     * @param valueFunction
     * @param fallbackSupplier
     * @param <R>              new type
     * @return new boxed R
     */
    public <R> Box<R> map(Function<T, R> valueFunction, Supplier<R> fallbackSupplier) {
        try {
            return this.map(valueFunction);
        } catch (Exception ignore) {
            return new Box<>(fallbackSupplier.get());
        }
    }

    @Override
    public int hashCode() {
        return this.value != null ? this.value.hashCode() : 0;
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && this.hashCode() == obj.hashCode();
    }
}

