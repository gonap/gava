/*
 * <https://gonn.org> [++]
 * Copyright (c) 2023 Gon Yi. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 */

package org.gonn.gava;

/**
 * A class implementing Nameable will return a name using getName() method.
 * With the override, this can return object specific.
 */
public interface Nameable {
    /**
     * Return the name of object if given.
     * Otherwise, return the class name.
     * @return String name
     */
    default String getName() {
        return this.getClass().getName();
    }
}
