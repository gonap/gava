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
    void trace(FnR<T> msg);

    void debug(FnR<T> msg);

    void info(FnR<T> msg);

    void warn(FnR<T> msg);

    void error(FnR<T> msg);

    void fatal(FnR<T> msg);
}
