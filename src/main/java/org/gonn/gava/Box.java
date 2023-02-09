/*
 * <https://gonn.org> [++]
 * Copyright (c) 2023 Gon Yi. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 */
package org.gonn.gava;


/**
 * Box an object just like the java.util.Optional
 *
 * @param <T> Type of the content to be boxed
 * @author Gon Yi
 * @version 0.0.2
 */
public class Box<T> {
    private T value;

    /**
     * Box object with T in it.
     *
     * @param t Boxed T.
     */
    public Box(T t) {
        this.value = t;
    }

    /**
     * Static constructor:
     *
     * @param obj A target object to be boxed.
     * @param <T> Type of the target object.
     * @return Boxed target object.
     */
    public static <T> Box<T> of(T obj) {
        return new Box<>(obj);
    }

    /**
     * Check if the box content is NULL.
     *
     * @return true if the content is NULL.
     */
    public boolean isEmpty() {
        return this.value == null;
    }

    /**
     * Check if the box content is NOT NULL.
     *
     * @return true if the content is full.
     */
    public boolean isFull() {
        return this.value != null;
    }

    /**
     * Get the box content.
     * When the box is empty, this will return null.
     *
     * @return Content of the box.
     */
    public T get() {
        return this.value;
    }

    /**
     * Get the box content.
     * If it is null, return the fallback instead.
     *
     * @param fallback A value to be returned when the box is empty.
     * @return Content of the box.
     */
    public T get(T fallback) {
        return this.value == null ? fallback : value;
    }

    /**
     * View or modifying the box content.
     * This is for a mutable content. Use thenSet() for immutable content.
     *
     * @param modFn A function that can view or modify the content.
     * @return self
     */
    public Box<T> then(FnT<T> modFn) {
        if (modFn != null && this.value != null)
            modFn.run(this.value);
        return this;
    }

    /**
     * Replace the box content.
     * This is to replace the immutable content in a box.
     * Content of the box will be replaced with the output of modFn.
     * NOTE: This is between same content type. Use "map" method in order to change to a different type.
     *
     * @param modFn A function that takes and returns type T data.
     * @return self
     */
    public Box<T> thenSet(FnTR<T, T> modFn) {
        if (modFn != null && this.value != null)
            this.value = modFn.run(this.value);
        return this;
    }

    /**
     * If the box is empty, returns the box with the fallback value
     *
     * @param fallback A fallback T when the box is empty.
     * @return Self
     */
    public Box<T> or(T fallback) {
        if (this.value == null)
            this.value = fallback;
        return this;
    }

    /**
     * If the box is empty, returns the box with the fallback value
     *
     * @param fallbackFn A function that returns fallback value T.
     * @return Self
     */
    public Box<T> orElse(FnR<T> fallbackFn) {
        if (this.value == null)
            this.value = fallbackFn.run();
        return this;
    }

    /**
     * If the box is empty (content is null), throw an exception.
     *
     * @param exception Exception to be thrown when the box is empty.
     * @param <E>       Any throwable object.
     * @return Self
     * @throws E Any Throwable exception
     */
    public <E extends Throwable> Box<T> orThrow(E exception) throws E {
        if (value == null) throw exception;
        return this;
    }

    /**
     * Converts box content to a new content in a box.
     *
     * @param mapFn Lambda function which takes current content, and returns a Box with new type of content.
     * @param <R>   New return type
     * @return new boxed R.
     */
    public <R> Box<R> map(FnTR<T, R> mapFn) {
        return new Box<>(mapFn.run(this.value));
    }

    /**
     * Converts box content to a new content in a box.
     * If the mapFn fails, this will return a box with the fallback value.
     *
     * @param mapFn    Lambda function which takes current content, and returns a Box with new type of content.
     * @param fallback Fallback value that will be boxed when mapFn throws an exception.
     *                 Note that if mapFn returns null, this fallback won't be called.
     * @param <R>      New return type
     * @return new boxed R
     */
    public <R> Box<R> map(FnTR<T, R> mapFn, R fallback) {
        try {
            return this.map(mapFn);
        } catch (Exception ignore) {
            return new Box<>(fallback);
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

