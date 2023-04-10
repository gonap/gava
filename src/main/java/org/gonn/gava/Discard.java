/* 
 * <https://gonn.org> [++]
 * Copyright (c) 2023 Gon Yi. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 */
package org.gonn.gava;

import java.io.OutputStream;
import java.io.PrintStream;


/**
 * Discard is a singleton class for null OutputStream and PrintStream
 * <code>
 *     OutputStream os = Discard.getOutputStream();
 *     PrintStream  ps = Discard.getPrintStream();
 * </code>
 */
public class Discard extends OutputStream {
    private static final OutputStream nullOutputStream = new Discard();
    private static PrintStream nullPrintStream;

    /**
     * Get a null output stream
     * @return OutputStream
     */
    public static OutputStream getOutputStream() {
        return nullOutputStream;
    }

    /**
     * Get a null print stream. This is not thread-safe but won't be an issue.
     * @return PrintStream
     */
    public static PrintStream getPrintStream() {
        if (nullPrintStream == null) nullPrintStream = new PrintStream(getOutputStream());
        return nullPrintStream;
    }

    private Discard() {}

    @Override public void write(int b) {}
    @Override public void write(byte b[]) {}
    @Override public void write(byte b[], int off, int len) {}
    @Override public void flush() {}
    @Override public void close() {}
    }

