package org.gonn.gava;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ObjectPool<T> {
    private final Deque<T> pool;
    private final Supplier<T> objectFactory;
    private final Consumer<T> resetter;
    private final Consumer<T> destroyer;
    private final int poolSize;
    private final AtomicInteger objectCreated;
    private final AtomicInteger objectRejected;
    private final AtomicInteger currentPoolSize;

    public ObjectPool(Supplier<T> objectFactory, Consumer<T> resetter, Consumer<T> destroyer, int poolSize) {
        if (objectFactory == null || resetter == null || destroyer == null) {
            throw new IllegalArgumentException("objectFactory, resetter, and destroyer cannot be null");
        }
        if (poolSize < 1) {
            throw new IllegalArgumentException("poolSize must be positive");
        }

        this.pool = new ConcurrentLinkedDeque<>();
        this.objectFactory = objectFactory;
        this.resetter = resetter;
        this.destroyer = destroyer;
        this.poolSize = poolSize;
        this.objectCreated = new AtomicInteger(0);
        this.objectRejected = new AtomicInteger(0);
        this.currentPoolSize = new AtomicInteger(0);
    }

    public T get() {
        T obj = this.pool.poll(); // returns null if empty, no exception
        if (obj == null) {
            this.objectCreated.incrementAndGet();
            return this.objectFactory.get();
        }
        this.currentPoolSize.decrementAndGet();
        return obj;
    }

    public void put(T object) {
        if (object == null) {
            throw new IllegalArgumentException("Cannot put null into pool");
        }

        final int size = currentPoolSize.get();
        this.resetter.accept(object);

        if (size < this.poolSize && this.currentPoolSize.compareAndSet(size, size + 1)) {
            this.pool.push(object);
        } else {
            this.objectRejected.incrementAndGet();
            this.destroyer.accept(object);
        }
    }

    public int size() {
        return this.currentPoolSize.get();  // More reliable than pool.size()
    }

    public int countCreated() {
        return this.objectCreated.get();
    }

    public int countRejected() {
        return this.objectRejected.get();
    }

    public void clear() {
        T obj;
        while ((obj = this.pool.poll()) != null) {
            this.currentPoolSize.decrementAndGet();
            this.destroyer.accept(obj);
        }
    }
}
