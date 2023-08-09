/*
 * <https://gonn.org> [++]
 * Copyright (c) 2023 Gon Yi. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 */
package org.gonn.gava;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Object Pool -- A Quick and Dirty Pool
 * @param <T>
 */
public class ObjectPool<T> {
    private final T[] pool;
    private final Supplier<T> creator;
    private final Consumer<T> cleaner;

    private volatile int objectThrown = 0;
    private volatile int objectCreated = 0;
    private volatile int objectReused = 0;

    public ObjectPool(Supplier<T[]> arrayMaker, Supplier<T> creator, Consumer<T> cleaner) {
        this.pool = arrayMaker.get();
        this.creator = creator;
        this.cleaner = cleaner;
    }

    public T get() {
        synchronized (this) {
            for (int i=0; i<this.pool.length; i++) {
                if (this.pool[i] != null) {
                    T tmp = this.pool[i];
                    this.pool[i] = null;
                    this.objectReused += 1;
                    return tmp;
                }
            }
            this.objectCreated += 1;
        }
        return this.creator.get();
    }

    public void put(T t) {
        this.cleaner.accept(t);
        synchronized (this) {
            for (int i=0; i<this.pool.length; i++) {
                if (this.pool[i] == null) {
                    this.pool[i] = t;
                    return;
                }
            }
            this.objectThrown += 1;
        }
    }

    public synchronized void clear() {
        for (int i=0; i<this.pool.length; i++) {
            this.pool[i] = null;
        }
    }

    /**
     * Returns the count of objects in the pool.  
     * Note that this isn't accurate
     */
    public int size() {
        int count = 0;
        for (int i=0; i<this.pool.length; i++) {
            if (this.pool[i] != null) count++;
        }
        return count;
    }

    public int capacity() { return this.pool.length; }
    public int created() { return this.objectCreated; }
    public int thrown() { return this.objectThrown; }
    public int reused() { return this.objectReused; }
}


