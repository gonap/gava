/*
 * Gava Library
 * <https://gonn.org> [++]
 * Copyright (c) 2023 Gon Yi. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 */
package org.gonn.gava;


/**
 * Simple line processor. This will be used when reading a text file. 
 *
 * @author Gon Yi
 * @version 0.0.1
 */
public interface LineProcessor {
    /**
     * Read line
     * @param lineNumber
     * @param line content
     */
    void process(int lineNumber, String line);
}
