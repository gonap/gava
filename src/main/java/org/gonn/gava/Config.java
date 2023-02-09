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
 * @version 0.0.2
 */
public class Config implements Storable<String, String>, AutoCloseable {
    private static DevLogger log;

    private static String COMMENT = "#";
    private static String SEPARATOR = "=";
    private static String SEPARATOR_OUT = " = ";

    private File f = null;
    private Map<String, String> conf = new LinkedHashMap<>();

    public Config(String filename) throws IOException {
        this(filename, null);
    }

    public Config(String filename, FnTTR<String, String, Boolean> lineFilter) throws IOException {
        this.f = new File(filename);
        // this.log = new Logger(StoreConfig.class, filename).testing();
        this.log = new DevLogger(Config.class, filename);

        if (!this.f.exists() && this.f.createNewFile()) this.log.info(() -> "Created a new file <" + filename);

        try (BufferedReader br = new BufferedReader(new FileReader(this.f))) {
            String line = br.readLine();
            int lineNumber = 0;
            for (; line != null; line = br.readLine()) {
                lineNumber++;
                if (line.startsWith(Config.COMMENT)) {
                    this.conf.put(line, null);
                    continue;
                }
                final int p = line.indexOf(Config.SEPARATOR);
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
                            this.conf.put(line, null); // treat it as a comment 
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
                        bw.append(Config.SEPARATOR_OUT);
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

    @Override
    public void set(String key, String rec) {
        if (rec != null && rec.indexOf('\n') > -1) {
            this.conf.put(key, rec.replace("\n", "\\n") + "\n");
        } else {
            this.conf.put(key, rec);
        }
    }

    @Override
    public boolean delete(String key) {
        return this.conf.remove(key) != null;
    }

    @Override
    public void close() throws IOException {
        this.save();
    }

    // public static void main(String[] args) throws Exception {
    //
    //     try (ConfigStore conf = new ConfigStore("_testConfig.prop", (k,v)->v.length()!=1)) {
    //         conf.set("name", "John");
    //         conf.set("updated", Gava.getEpochString(System.currentTimeMillis(), -6));
    //         // conf.set("coffee", "black");
    //         System.out.println("delete(coffee): " + conf.delete("coffee"));
    //
    //         conf.forEach((k, v) -> {
    //             if (v==null) {
    //                 System.out.printf("SKIP <%s>%s", k, System.lineSeparator());
    //             } else {
    //                 System.out.printf("     [%-10s] %s%s", k, v, System.lineSeparator());
    //             }
    //         });
    //     }
    // }
}

