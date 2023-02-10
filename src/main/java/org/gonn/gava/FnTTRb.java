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
 * @see org.gonn.gava.FnTTR
 */
@FunctionalInterface
public interface FnTTRb<T1, T2> {
    boolean run(T1 t1, T2 t2);
}

