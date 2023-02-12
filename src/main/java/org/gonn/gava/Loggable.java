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
 * @version 1.0.2
 * @see <a href="https://gonn.org">gonn.org</a>
 */
public interface Loggable<T> {
    // ====================================================================================================
    // LOGGING
    // Log message takes a supplier function to reduce allocation
    // ====================================================================================================
    void trace(Fx01<T> msg);

    void debug(Fx01<T> msg);

    void info(Fx01<T> msg);

    void warn(Fx01<T> msg);

    void error(Fx01<T> msg);

    void fatal(Fx01<T> msg);
}
