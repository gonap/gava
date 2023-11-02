/*
 * Gava Library
 * <https://gonn.org> [++]
 * Copyright (c) 2023 Gon Yi. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 */
package org.gonn.gava;

/**
 * Minimal Logger Interface
 *
 * @author Gon Yi
 * @version 1.0.2
 * @see <a href="https://gonn.org">gonn.org</a>
 */
public interface Loggable {
    default String getName() {
        return this.getClass().getSimpleName();
    }

    // ====================================================================================================
    // LOGGING
    // Log message takes a supplier function to reduce allocation
    // ====================================================================================================
    default void trace(String msg) {
        System.out.printf("TRACE [%s] %s%n", this.getName(), msg);
    }

    default void debug(String msg) {
        System.out.printf("DEBUG [%s] %s%n", this.getName(), msg);
    }

    default void info(String msg) {
        System.out.printf("INFO  [%s] %s%n", this.getName(), msg);
    }

    default void warn(String msg) {
        System.err.printf("WARN  [%s] %s%n", this.getName(), msg);
    }

    default void error(String msg) {
        System.err.printf("ERROR [%s] %s%n", this.getName(), msg);
    }

    default void fatal(String msg) {
        System.err.printf("FATAL [%s] %s%n", this.getName(), msg);
    }
}
