/*
 * <https://gonn.org> [++]
 * Copyright (c) 2023 Gon Yi. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 */
package org.gonn.gava;

/**
 * Lambda function that returns (primitive) integer.
 * While R means return of generic value, Rb means return of integer.
 *
 * @author Gon Yi
 * @version 0.0.1
 * @see FxBool
 */
@FunctionalInterface
public interface FxInt<T> {
    int run(T t);
}

