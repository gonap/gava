/*
 * <https://gonn.org> [++]
 * Copyright (c) 2023 Gon Yi. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 */
package org.gonn.gava;

/**
 * Takes an object and create an ObjectModifier. 
 * With the `modify(T)` method, this will modify the object. 
 * This is meant to be used to create a module or a fix.
 * @param <T>
 * @author Gon Yi
 * @version 0.0.1
 */
@FunctionalInterface
public interface ObjectModifier<T> { 
    /**
     * Returns a name of modifier.
     * This is meant to be overriden by the user.  
     * @return Modifier name
     */
    default String getName() {
        return "ObjectModifier";
    }

    /**
     * Modifies T object t.
     * @param t
     */
    void modify(T t);
}
