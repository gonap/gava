/*
 * <https://gonn.org> [++]
 * Copyright (c) 2023 Gon Yi. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 */
package org.gonn.gava;

/**
 * Minimal Logger Interface
 *
 * @param <T> Any value that supplier will return
 * @author Gon Yi
 * @version 1.0.1
 * @see <a href="https://gonn.org">gonn.org</a>
 */
public interface Loggable<T> {
    // ====================================================================================================
    // LOGGING
    // Log message takes a supplier function to reduce allocation
    // ====================================================================================================
    void debug(FnR<T> msg);

    void info(FnR<T> msg);

    void warn(FnR<T> msg);

    void error(FnR<T> msg);

    // ====================================================================================================
    // OPTIONAL METHODS
    // Since trace and fatal aren't used often, it is not necessary to define it.
    // But, if those are used without being defined, the nearest method will be called.
    // (e.g. trace -> debug, fatal -> error)
    // for error and fatal methods, `int skip` is for skipping callers (stack)
    // ====================================================================================================
    default void trace(FnR<T> msg) {
        this.debug(msg);
    }

    default void fatal(FnR<T> msg) {
        this.error(msg);
    }

    // OPTIONAL: SKIP STACK
    default void warn(FnR<T> msg, int skip) {
        this.warn(msg);
    }

    default void error(FnR<T> msg, int skip) {
        this.error(msg);
    }

    default void fatal(FnR<T> msg, int skip) {
        this.fatal(msg);
    }
}
