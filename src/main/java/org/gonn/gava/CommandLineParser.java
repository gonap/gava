package org.gonn.gava;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class CommandLineParser {
    public static final Supplier<String> DEFAULT_PROMPTER = () -> "> ";
    public final PrintStream output;
    private final BufferedReader buffer = new BufferedReader(new InputStreamReader(System.in));
    private Supplier<String> prompter;
    private boolean stop;

    public CommandLineParser(PrintStream output) {
        this.prompter = DEFAULT_PROMPTER;
        this.output = output;
        this.stop = false;
    }

    public CommandLineParser() {
        this(System.out);
    }

    public static CommandLineParser newInstance(PrintStream output) {
        return new CommandLineParser(output);
    }

    public static CommandLineParser newInstance() {
        return new CommandLineParser();
    }

    public CommandLineParser setPrompt(Supplier<String> prompter) {
        this.prompter = (prompter == null) ? DEFAULT_PROMPTER : prompter;
        return this;
    }

    public CommandLineParser setPrompt(String prompt) {return this.setPrompt(() -> prompt);}

    public CommandLineParser stop() {
        this.stop = true;
        return this;
    }

    public void execute(BiConsumer<CommandLineParser, String> handler) {
        String line;
        while (!this.stop) {
            try {
                this.output.print(this.prompter.get());
                line = this.buffer.readLine();
                if (line == null || line.trim().isEmpty()) continue;
                handler.accept(this, line);
            } catch (Exception e) {
                this.output.println("Error: " + e.getMessage());
            }
        }
    }
}
