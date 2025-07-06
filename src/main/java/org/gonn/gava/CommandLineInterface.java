package org.gonn.gava;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class CommandLineInterface {
    public static final Supplier<String> DEFAULT_PROMPTER = () -> "> ";
    public final PrintStream output;
    private final BufferedReader buffer = new BufferedReader(new InputStreamReader(System.in));
    private Supplier<String> prompter;
    private boolean stop;

    public CommandLineInterface(PrintStream output) {
        this.prompter = DEFAULT_PROMPTER;
        this.output = output;
        this.stop = false;
    }

    public CommandLineInterface() {
        this(System.out);
    }

    public static CommandLineInterface newInstance(PrintStream output) {
        return new CommandLineInterface(output);
    }

    public static CommandLineInterface newInstance() {
        return new CommandLineInterface();
    }

    public CommandLineInterface setPrompt(Supplier<String> prompter) {
        this.prompter = (prompter == null) ? DEFAULT_PROMPTER : prompter;
        return this;
    }

    public CommandLineInterface setPrompt(String prompt) {return this.setPrompt(() -> prompt);}

    public CommandLineInterface stop() {
        this.stop = true;
        return this;
    }

    public void execute(BiConsumer<CommandLineInterface, String> handler) {
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
