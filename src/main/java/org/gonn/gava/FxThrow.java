/*
 * <https://gonn.org> [++]
 * Copyright (c) 2023 Gon Yi. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 */
package org.gonn.gava;

/**
 * Fx11-type functional interface that can throw an exception 
 *
 * @param <T> input type
 * @param <R> return type
 * @author Gon Yi
 * @version 0.0.1
 * @see Fx11
 */
@FunctionalInterface
public interface FxThrow<T, R> {
    R run(T t) throws Exception;
}
