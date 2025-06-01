package org.gonn.gava;

import java.io.PrintStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public class EntryPoint {
    private final Map<String, Executor> executors;

    public EntryPoint() {this.executors = new LinkedHashMap<>();}

    public static EntryPoint newInstance() {return new EntryPoint();}

    public EntryPoint add(Executor executor) {
        if (executor == null) throw new IllegalArgumentException("executor cannot be null");
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
            formatter = exec -> String.format(" - %-10s  %s", exec.getName(), exec.getDescription());
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

}
