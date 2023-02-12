/*
 * <https://gonn.org> [++]
 * Copyright (c) 2023 Gon Yi. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 */
package org.gonn.gava;

/**
 * Lambda function that takes param T1 and T2, and returns nothing.
 * Similar to java.util.function package's, but functional interface in Gava has `run()` method.
 *
 * @param <T1> input type 1
 * @param <T2> input type 2
 * @author Gon Yi
 * @version 0.0.1
 */
@FunctionalInterface
public interface Fx20<T1, T2> {
    void run(T1 t1, T2 t2);
}
