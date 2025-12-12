package org.gonn.gava;

import java.io.PrintStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.BiConsumer;

public class EntryPoint {
    private final Map<String, Executor> executors;
    private int maxCommandSize = 0;

    public EntryPoint() {this.executors = new LinkedHashMap<>();}

    public static EntryPoint newInstance() {return new EntryPoint();}

    public EntryPoint add(Executor executor) {
        if (executor == null) throw new IllegalArgumentException("executor cannot be null");
        if (executor.getName().length() > this.maxCommandSize) {
            this.maxCommandSize = executor.getName().length();
        }
        this.executors.put(executor.getName(), executor);
        return this;
    }

    public EntryPoint add(String name, Consumer<String[]> executor, String description) {
        return this.add(new Executor(name, executor, description));
    }

    public EntryPoint add(String name, Class<?> clazz, String description) {
        if (description == null || description.isEmpty()) description = clazz.getCanonicalName();
        return this.add(new Executor(name, args -> {
            try {
                clazz.getMethod("main", String[].class).invoke(null, (Object) args);
            } catch (Exception e) {
                throw new RuntimeException("Failed to execute the main method of " + clazz.getName(), e);
            }
        }, description));
    }

    public EntryPoint add(String name, Class<?> clazz) {
        return this.add(name, clazz, null);
    }

    public void printExecutors(PrintStream ps, Function<Executor, String> formatter) {
        if (formatter == null) {
            formatter = exec -> " - " + Stu.padRight(exec.getName(), this.maxCommandSize, ' ') +
                "   " + exec.getDescription();
        }
        ps.println("Executors:");
        for (Executor exec : this.executors.values()) {
            ps.println(formatter.apply(exec));
        }
    }

    public void exec(String[] args) {
        if (args == null || args.length == 0 || !this.executors.containsKey(args[0])) {
            System.out.println("Usage: <executor> [args...]");
            this.printExecutors(System.out, null);
            return;
        }

        this.executors.get(args[0]).execute(Stu.subset(args, 1, 0, String[]::new));
    }

    public static class Executor {
        private final String name;
        private final Consumer<String[]> executor;
        private final String description;

        public Executor(String name, Consumer<String[]> executor, String description) {
            if (name == null || name.isEmpty() || executor == null) {
                throw new IllegalArgumentException("Name/executor cannot be null/empty");
            }
            this.name = name;
            this.executor = executor;
            this.description = description;
        }

        public String getName() {return this.name;}

        public Consumer<String[]> getExecutor() {return this.executor;}

        public void execute(String[] args) {this.getExecutor().accept(args);}

        public String getDescription() {return this.description;}
    }

    public static void loadInputStream(BiConsumer<Integer, String> loader) throws IOException {
        loadInputStream(System.in, loader);
    }

    public static int loadInputStream(InputStreamReader isr, BiConsumer<Integer, String> loader) throws IOException {
        try (BufferedReader br = new BufferedReader(isr)) {
            if (isr.ready()) {
                String line;
                int lineNumber = 0;
                while ((line = br.readLine()) != null) {
                    lineNumber++;
                    loader.accept(lineNumber, line);
                }
                return lineNumber;
            }
        }
        return 0;
    }

    public static int loadInputStream(InputStream inputStream, BiConsumer<Integer, String> loader) throws IOException {
        try (InputStreamReader is = new InputStreamReader(inputStream)) {
            return loadInputStream(is, loader);
        }
    }
}
