package org.gonn.gava;

import java.util.Deque;
import java.util.LinkedList;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ObjectPool<T> {
    private final Deque<T> pool;
    private final Supplier<T> objectFactory;
    private final Consumer<T> initializer;
    private final int poolSize;
    private int objectCreated = 0;
    private int objectDiscarded = 0;

    public ObjectPool(Supplier<T> objectFactory, Consumer<T> initializer, int poolSize) {
        this.pool = new LinkedList<>();
        this.objectFactory = objectFactory;
        this.initializer = initializer;
        this.poolSize = poolSize;
    }

    public synchronized T get() {
        if (this.pool.isEmpty()) {
            this.objectCreated++;
            return this.objectFactory.get();
        }
        return this.pool.pop();
    }

    public synchronized void release(T object) {
        if (object == null) return;
        if (this.pool.size() < this.poolSize) {
            this.initializer.accept(object);
            this.pool.push(object);
        } else {
            this.objectDiscarded++;
        }
    }

    public synchronized int size() {return this.pool.size();}

    public synchronized int countCreated() {return this.objectCreated;}

    public synchronized int countDiscarded() {return this.objectDiscarded;}
}
