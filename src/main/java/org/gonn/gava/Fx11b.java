/*
 * <https://gonn.org> [++]
 * Copyright (c) 2023 Gon Yi. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 */
package org.gonn.gava;

/**
 * Lambda function that returns (primitive) boolean.
 * While R means return of generic value, Rb means return of boolean.
 *
 * @author Gon Yi
 * @version 0.0.1
 * @see Fx11
 */
@FunctionalInterface
public interface Fx11b<T> {
    boolean run(T t);
}

