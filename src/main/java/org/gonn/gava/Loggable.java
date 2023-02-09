package org.gonn.gava;

import java.util.function.Supplier;

/**
 * Minimal Logger Interface
 *
 * @param <T> Any value that supplier will return
 * @author Gon Yi
 * @version 1.0.1
 * @link https://gonn.org
 */
public interface Loggable<T> {
    // ====================================================================================================
    // LOGGING
    // Log message takes a supplier function to reduce allocation
    // ====================================================================================================
    void debug(Supplier<T> msg);

    void info(Supplier<T> msg);

    void warn(Supplier<T> msg);

    void error(Supplier<T> msg);

    // ====================================================================================================
    // OPTIONAL METHODS
    // Since trace and fatal aren't used often, it is not necessary to define it.
    // But, if those are used without being defined, the nearest method will be called.
    // (e.g. trace -> debug, fatal -> error)
    // for error and fatal methods, `int skip` is for skipping callers (stack)
    // ====================================================================================================
    default void trace(Supplier<T> msg) {
        this.debug(msg);
    }

    default void fatal(Supplier<T> msg) {
        this.error(msg);
    }

    // OPTIONAL: SKIP STACK
    default void warn(Supplier<T> msg, int skip) {
        this.warn(msg);
    }

    default void error(Supplier<T> msg, int skip) {
        this.error(msg);
    }

    default void fatal(Supplier<T> msg, int skip) {
        this.fatal(msg);
    }
}
