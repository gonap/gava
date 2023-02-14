/*
 * <https://gonn.org> [++]
 * Copyright (c) 2023 Gon Yi. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 */
package org.gonn.gava;

/**
 * A lambda functino that returns the data with same type with input 
 * (e.g. Fx11 with type T and T, instead of T1 and T2)
 *
 * @param <T> input and output type
 * @author Gon Yi
 * @version 0.0.1
 * @see Fx11
 */
@FunctionalInterface
public interface FxModify<T> {
    T run(T t);
}
