/*
 * <https://gonn.org> [++]
 * Copyright (c) 2023 Gon Yi. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 */
package org.gonn.gava;

/**
 * Lambda function that takes a param T.
 * Similar to java.util.function package's, but functional interface in Gava has `run()` method.
 *
 * @param <T> input type
 * @author Gon Yi
 * @version 0.0.1
 */
@FunctionalInterface
public interface FnT<T> {
    void run(T t);
}
