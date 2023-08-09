/*
 * <https://gonn.org> [++]
 * Copyright (c) 2023 Gon Yi. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 */
package org.gonn.gava;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.util.function.BiConsumer;

/**
 * Line Reader 
 *
 * @author Gon Yi
 * @version 0.0.1
 */
public class LineReader {
    public static int fromInputStream(InputStream is, BiConsumer<Integer, String> proc) throws IOException {
        try(BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            int lineNo = 0;
            for (String line; (line = br.readLine()) != null; ) {
                proc.accept(++lineNo, line);
            }
            return lineNo;
        }
    }

    public static int fromFile(String filename, BiConsumer<Integer, String> proc) throws IOException {
        try(FileInputStream fis = new FileInputStream(filename)) {
            return fromInputStream(fis, proc);
        }
    }

    public static int fromResource(String filename, BiConsumer<Integer, String> proc) throws IOException {
        InputStream is = LineReader.class.getResourceAsStream(filename);
        return fromInputStream(is, proc);
    }
}
