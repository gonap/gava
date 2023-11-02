/*
 * Gava Library
 * <https://gonn.org> [++]
 * Copyright (c) 2023 Gon Yi. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 */
package org.gonn.gava;

/**
 * When I am too lazy to create a getter and setter, using this will help. Use it as a <b>public static</b> variable,
 * so it (Boxed) can't be reassigned. However, using get() and set() methods of Boxed, a user can replace the value.
 * Note that once lock() method is called, set() method won't work.
 * <pre><code>
 *     public class Hello() {
 *         public final Boxed&lt;String&gt; name = Boxed.of("");
 *     }
 * </code></pre>
 *
 * @param <T> any type to store
 */
public class Boxed<T> {
    private T content;  // holds any value
    private boolean lock;

    /**
     * If an object is given to the constructor, it will create box using that value. Otherwise, null will be used.
     * For instance, <code>new Boxed("Hello")</pre> equals to <code>new Boxed().set("Hello")</code>
     *
     * @param content any content to store
     */
    public Boxed(T content) {
        this.content = content;
    }

    public Boxed() {
        this(null);
    }

    public static <T> Boxed<T> of(T t) {
        return new Boxed<T>(t);
    }

    /**
     * Get underlying content if exists. Otherwise return a fallback value.
     *
     * @param fallback A value to return when the content is null.
     * @return the content value if exists, otherwise returns given fallback.
     */
    public T get(T fallback) {
        return (this.content != null) ? this.content : fallback;
    }

    public T get() {
        return this.get(null);
    }

    /**
     * Set current stored content with given content.
     * If no content is given, null will be stored.
     *
     * @param content any content to store
     * @return self
     */
    public Boxed<T> set(T content) {
        if (!this.isLocked()) this.content = content;
        return this;
    }

    public Boxed<T> set() {
        return this.set(null);
    }

    public boolean isLocked() {
        return this.lock;
    }

    /**
     * Lock the Boxed content. Once locked, set() won't work.
     * Note that this is not thread-safe.
     *
     * @return self
     */
    public Boxed<T> lock() {
        this.lock = true;
        return this;
    }

    /**
     * Unlock locked content. This is for other classes expanding Boxed.
     * Note that this is not thread-safe.
     *
     * @return self
     */
    protected Boxed<T> unlock() {
        this.lock = false;
        return this;
    }

    @Override
    public String toString() {
        return (this.content == null) ? "null" : this.content.toString();
    }
}
