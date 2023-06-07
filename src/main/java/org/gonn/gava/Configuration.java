/*
 * <https://gonn.org> [++]
 * Copyright (c) 2023 Gon Yi. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 */
package org.gonn.gava;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;


/**
 * Storing Configuration Property File
 *
 * @author Gon Yi
 * @version 0.0.3
 */
public class Configuration implements Storable<String, String>, AutoCloseable {
    private static TempLogger log;

    private static String COMMENT = "#";
    private static String SEPARATOR = "=";
    private static String SEPARATOR_OUT = " = ";

    private File f = null;
    private Map<String, String> conf = new LinkedHashMap<>();

    public Configuration(String filename) throws IOException {
        this(filename, null);
    }

    public Configuration(String filename, Fx21<String, String, Boolean> lineFilter) throws IOException {
        this.f = new File(filename);
        this.log = new TempLogger(Configuration.class, filename);

        if (!this.f.exists() && this.f.createNewFile()) this.log.info(() -> "Created a new file <" + filename);

        try (BufferedReader br = new BufferedReader(new FileReader(this.f))) {
            String line = br.readLine();
            int lineNumber = 0;
            for (; line != null; line = br.readLine()) {
                lineNumber++;
                if (line.startsWith(Configuration.COMMENT)) {
                    continue;
                }
                final int p = line.indexOf(Configuration.SEPARATOR);
                final int tmpLineNumber = lineNumber;
                if (p > -1) {
                    String key = line.substring(0, p).trim();
                    String val = line.substring(p + 1).trim();

                    // handling double quote
                    if (val.length() > 1 && val.startsWith("\"") && val.endsWith("\"")) {
                        val = val.substring(1, val.length() - 1);
                    }
                    if (lineFilter != null) {
                        if (lineFilter.run(key, val)) {
                            // add
                            this.conf.put(key, val);
                        } else {
                            this.log.trace(() -> "Per lineFilter, skip line " + tmpLineNumber + ", key=" + key);
                        }
                    } else {
                        this.conf.put(key, val);
                    }
                } else {
                    final String tmpLine = line;
                    log.debug(() -> "Ignored line: " + tmpLineNumber + ": " + tmpLine);
                }
            }
        }
    }

    public void forEach(BiConsumer<String, String> f) {
        this.conf.forEach(f);
    }

    public void save() throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(this.f))) {
            this.conf.forEach((k, v) -> {
                try {
                    bw.append(k);
                    if (v != null) {
                        bw.append(Configuration.SEPARATOR_OUT);
                        if (v.indexOf(' ') > -1) {
                            bw.append('"').append(v).append('"');
                        } else {
                            bw.append(v);
                        }
                    }
                    bw.append('\n');
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    @Override
    public String get(String key) {
        return this.conf.get(key);
    }

    public String get(String key, String fallback) {
        return this.conf.getOrDefault(key, fallback);
    }

    @Override
    public boolean set(String key, String rec) {
        if (rec != null && rec.indexOf('\n') > -1) {
            this.conf.put(key, rec.replace("\n", "\\n") + "\n");
        } else {
            this.conf.put(key, rec);
        }
        return true;
    }

    @Override
    public boolean delete(String key) {
        return this.conf.remove(key) != null;
    }

    @Override
    public void close() throws IOException {
        conf.clear();
    }
}

