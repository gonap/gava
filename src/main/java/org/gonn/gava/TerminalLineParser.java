/*
 * <https://gonn.org> (+++)
 * Copyright (c) 2022-2026 Gon Yi. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 */
package org.gonn.gava;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * TerminalLineReader
 * <p>
 * An enhanced command-line reader for Linux/macOS that supports:
 * <ul>
 *   <li>Up / Down arrow keys for command history navigation</li>
 *   <li>Left / Right arrow keys for cursor movement within the line</li>
 *   <li>Alt+B / Alt+F for backward/forward word jump</li>
 *   <li>Ctrl+A / Ctrl+E for jump to start/end of line</li>
 *   <li>Ctrl+K to delete from cursor to end of line</li>
 *   <li>Backspace / Delete for character deletion</li>
 * </ul>
 * The terminal is temporarily put into raw mode (via {@code stty}) so that
 * individual keystrokes can be intercepted without waiting for Enter.
 * The original terminal settings are restored when the reader is stopped or
 * when the JVM shuts down.
 * </p>
 * <p>
 * <b>Note:</b> This class requires a POSIX-compatible system ({@code stty} must
 * be available on {@code PATH}).  It is designed for Linux and macOS.
 * </p>
 *
 * @author Gon Yi
 */
public class TerminalLineReader {

    // -----------------------------------------------------------------------
    // Constants / ANSI helpers
    // -----------------------------------------------------------------------

    /** Default prompt supplier – shows {@code "> "}. */
    public static final Supplier<String> DEFAULT_PROMPTER = () -> "> ";

    // ANSI escape prefix
    private static final String ESC = "\033[";

    // -----------------------------------------------------------------------
    // Fields
    // -----------------------------------------------------------------------

    /** Output stream used for all terminal I/O (prompt, echoed characters). */
    public final PrintStream output;

    private final InputStream input;
    private Supplier<String> prompter;
    private boolean stop;

    /** Ordered history of previously submitted lines. */
    private final List<String> history = new ArrayList<>();

    /** Maximum number of history entries to keep. */
    private int maxHistory = 500;

    // -----------------------------------------------------------------------
    // Constructor / factory
    // -----------------------------------------------------------------------

    /**
     * Creates a new {@code TerminalLineReader} using the supplied streams.
     *
     * @param output stream to print the prompt and echoed text
     * @param input  raw byte stream to read keystrokes from (typically {@link System#in})
     */
    public TerminalLineReader(PrintStream output, InputStream input) {
        this.output = output;
        this.input = input;
        this.prompter = DEFAULT_PROMPTER;
        this.stop = false;
    }

    /** Creates a new instance that writes to {@link System#out} and reads from {@link System#in}. */
    public TerminalLineReader() {
        this(System.out, System.in);
    }

    /** Factory method – equivalent to {@code new TerminalLineReader()}. */
    public static TerminalLineReader newInstance() {
        return new TerminalLineReader();
    }

    /** Factory method with explicit streams. */
    public static TerminalLineReader newInstance(PrintStream output, InputStream input) {
        return new TerminalLineReader(output, input);
    }

    // -----------------------------------------------------------------------
    // Configuration
    // -----------------------------------------------------------------------

    /** Sets the prompt supplier. Passing {@code null} restores the default. */
    public TerminalLineReader setPrompt(Supplier<String> prompter) {
        this.prompter = (prompter == null) ? DEFAULT_PROMPTER : prompter;
        return this;
    }

    /** Sets a static prompt string. */
    public TerminalLineReader setPrompt(String prompt) {
        return setPrompt(() -> prompt);
    }

    /** Sets the maximum number of history entries (default: 500). */
    public TerminalLineReader setMaxHistory(int max) {
        this.maxHistory = (max > 0) ? max : 1;
        return this;
    }

    /** Returns a read-only snapshot of the current history list. */
    public List<String> getHistory() {
        return new ArrayList<>(history);
    }

    // -----------------------------------------------------------------------
    // Lifecycle
    // -----------------------------------------------------------------------

    /**
     * Signals the read loop to stop after the current line is processed.
     * This method is safe to call from within the {@code handler} callback.
     */
    public TerminalLineReader stop() {
        this.stop = true;
        return this;
    }

    // -----------------------------------------------------------------------
    // Main read loop
    // -----------------------------------------------------------------------

    /**
     * Enters the interactive read loop.
     * <p>
     * When running on a real TTY (Linux/macOS terminal) the terminal is put into
     * raw mode so that individual keystrokes are delivered immediately, enabling
     * history navigation, cursor movement, and word-jump shortcuts.
     * </p>
     * <p>
     * When the process is <em>not</em> attached to a real TTY (e.g. running inside
     * IntelliJ's built-in Run console, or with piped input) a simple cooked-mode
     * fallback is used: lines are read with {@link BufferedReader#readLine()}, which
     * avoids the double-echo that would otherwise occur because the console already
     * echoes every character.
     * </p>
     *
     * @param handler callback invoked with (this, line) for every non-empty line
     */
    public void execute(BiConsumer<TerminalLineReader, String> handler) {
        String savedStty = saveStty();
        boolean isTty = !savedStty.isEmpty();

        if (!isTty) {
            // Not a real TTY – fall back to simple line reading to avoid double-echo
            cookedLoop(handler);
            return;
        }

        // Restore on JVM shutdown (e.g. Ctrl+C)
        final String finalSavedStty = savedStty;
        Thread shutdownHook = new Thread(() -> restoreStty(finalSavedStty));
        Runtime.getRuntime().addShutdownHook(shutdownHook);

        try {
            enableRawMode();
            readLoop(handler);
        } finally {
            restoreStty(savedStty);
            try {
                Runtime.getRuntime().removeShutdownHook(shutdownHook);
            } catch (IllegalStateException ignored) {
                // JVM already shutting down
            }
        }
    }

    // -----------------------------------------------------------------------
    // Cooked-mode fallback (non-TTY environments such as IntelliJ console)
    // -----------------------------------------------------------------------

    /**
     * Simple line-reading loop used when stdin is not a real TTY.
     * The host environment (IDE console, pipe) already handles echo and line
     * editing, so we just read complete lines and dispatch them.
     */
    private void cookedLoop(BiConsumer<TerminalLineReader, String> handler) {
        BufferedReader br = new BufferedReader(new InputStreamReader(input));
        printPrompt();
        while (!this.stop) {
            String line;
            try {
                line = br.readLine();
            } catch (IOException e) {
                output.println("Read error: " + e.getMessage());
                break;
            }
            if (line == null) break; // EOF
            String text = line.trim();
            if (!text.isEmpty()) {
                addHistory(text);
                handler.accept(this, text);
            }
            if (!this.stop) printPrompt();
        }
    }

    // -----------------------------------------------------------------------
    // Internal – read loop
    // -----------------------------------------------------------------------

    private void readLoop(BiConsumer<TerminalLineReader, String> handler) {
        // Mutable line buffer (using StringBuilder for the actual text)
        StringBuilder line = new StringBuilder();
        // Cursor position within line (0 = before first char)
        int cursor = 0;

        // History navigation state
        int historyIndex = history.size(); // points "after" last entry (new line)
        String savedDraft = "";            // draft saved when user starts browsing history

        printPrompt();

        while (!this.stop) {
            int b;
            try {
                b = input.read();
            } catch (IOException e) {
                output.println("\r\nRead error: " + e.getMessage());
                break;
            }

            if (b == -1) {
                // EOF (Ctrl+D on empty line or stream closed)
                output.print("\r\n");
                break;
            }

            // ---- Enter (CR or LF) ----------------------------------------
            if (b == '\r' || b == '\n') {
                // Move cursor to end of text before newline to avoid trailing gap
                moveCursorToCol(prompter.get().length() + line.length() + 1);
                output.print("\r\n");
                String text = line.toString().trim();
                if (!text.isEmpty()) {
                    addHistory(text);
                    handler.accept(this, text);
                }
                // Reset for next line
                line.setLength(0);
                cursor = 0;
                historyIndex = history.size();
                savedDraft = "";
                if (!this.stop) printPrompt();
                continue;
            }

            // ---- Ctrl+C --------------------------------------------------
            if (b == 3) {
                output.print("^C\r\n");
                line.setLength(0);
                cursor = 0;
                historyIndex = history.size();
                savedDraft = "";
                if (!this.stop) printPrompt();
                continue;
            }

            // ---- Ctrl+D (on non-empty line – delete char under cursor) ----
            if (b == 4) {
                if (line.length() == 0) {
                    // Treat as EOF
                    output.print("\r\n");
                    break;
                }
                if (cursor < line.length()) {
                    line.deleteCharAt(cursor);
                    redrawLine(line.toString(), cursor);
                }
                continue;
            }

            // ---- Backspace (127) or Ctrl+H (8) ---------------------------
            if (b == 127 || b == 8) {
                if (cursor > 0) {
                    cursor--;
                    line.deleteCharAt(cursor);
                    redrawLine(line.toString(), cursor);
                }
                continue;
            }

            // ---- Ctrl+A – go to beginning of line ------------------------
            if (b == 1) {
                cursor = 0;
                moveCursorToCol(prompter.get().length() + 1);
                continue;
            }

            // ---- Ctrl+E – go to end of line ------------------------------
            if (b == 5) {
                cursor = line.length();
                moveCursorToCol(prompter.get().length() + cursor + 1);
                continue;
            }

            // ---- Ctrl+K – kill to end of line ----------------------------
            if (b == 11) {
                if (cursor < line.length()) {
                    line.delete(cursor, line.length());
                    redrawLine(line.toString(), cursor);
                }
                continue;
            }

            // ---- Ctrl+W – delete previous word ---------------------------
            if (b == 23) {
                if (cursor > 0) {
                    int newCursor = wordStartBefore(line.toString(), cursor);
                    line.delete(newCursor, cursor);
                    cursor = newCursor;
                    redrawLine(line.toString(), cursor);
                }
                continue;
            }

            // ---- ESC sequence --------------------------------------------
            if (b == 27) {
                int[] intOut = new int[]{cursor, historyIndex};
                String[] draftOut = new String[]{savedDraft};
                cursor = handleEscape(line, cursor, intOut, historyIndex, draftOut);
                historyIndex = intOut[1];
                savedDraft = draftOut[0];
                continue;
            }

            // ---- Printable ASCII -----------------------------------------
            if (b >= 32 && b < 127) {
                line.insert(cursor, (char) b);
                cursor++;
                redrawLine(line.toString(), cursor);
            }
            // Other control characters are silently ignored
        }
    }

    // -----------------------------------------------------------------------
    // Escape sequence handler
    // -----------------------------------------------------------------------

    /**
     * Reads the rest of an ESC sequence and acts on it.
     *
     * @param line          current line buffer
     * @param cursor        current cursor position
     * @param inOut         two-element array: [0]=cursor out, [1]=historyIndex in/out
     * @param historyIndex  current history browsing index
     * @param draftOut      one-element array holding the saved draft; updated when history browsing starts
     * @return updated cursor position
     */
    private int handleEscape(StringBuilder line, int cursor, int[] inOut,
                              int historyIndex, String[] draftOut) {
        int next;
        try {
            // Non-blocking peek with a short timeout is not straightforward with
            // plain InputStream; we rely on the fact that escape sequences arrive
            // as a burst from the terminal emulator.
            next = input.read();
        } catch (IOException e) {
            return cursor;
        }

        if (next == -1) return cursor;

        // ---- Alt+B (ESC b) – backward word jump --------------------------
        if (next == 'b') {
            cursor = wordStartBefore(line.toString(), cursor);
            moveCursorToCol(prompter.get().length() + cursor + 1);
            inOut[0] = cursor;
            inOut[1] = historyIndex;
            return cursor;
        }

        // ---- Alt+F (ESC f) – forward word jump ---------------------------
        if (next == 'f') {
            cursor = wordEndAfter(line.toString(), cursor);
            moveCursorToCol(prompter.get().length() + cursor + 1);
            inOut[0] = cursor;
            inOut[1] = historyIndex;
            return cursor;
        }

        // ---- CSI sequence (ESC [) ----------------------------------------
        if (next == '[') {
            int csi;
            try {
                csi = input.read();
            } catch (IOException e) {
                return cursor;
            }

            switch (csi) {
                case 'A': // Up arrow – previous history entry
                    if (!history.isEmpty()) {
                        if (historyIndex == history.size()) {
                            // Save the current draft before browsing
                            draftOut[0] = line.toString();
                        }
                        if (historyIndex > 0) {
                            historyIndex--;
                            line.setLength(0);
                            line.append(history.get(historyIndex));
                            cursor = line.length();
                            redrawLine(line.toString(), cursor);
                        }
                    }
                    inOut[1] = historyIndex;
                    break;

                case 'B': // Down arrow – next history entry
                    if (historyIndex < history.size()) {
                        historyIndex++;
                        line.setLength(0);
                        if (historyIndex == history.size()) {
                            line.append(draftOut[0]);
                        } else {
                            line.append(history.get(historyIndex));
                        }
                        cursor = line.length();
                        redrawLine(line.toString(), cursor);
                    }
                    inOut[1] = historyIndex;
                    break;

                case 'C': // Right arrow
                    if (cursor < line.length()) {
                        cursor++;
                        output.print(ESC + "C");
                    }
                    break;

                case 'D': // Left arrow
                    if (cursor > 0) {
                        cursor--;
                        output.print(ESC + "D");
                    }
                    break;

                case '3': {
                    // ESC [ 3 ~ → Delete key
                    try { input.read(); } catch (IOException ignored) {} // consume '~'
                    if (cursor < line.length()) {
                        line.deleteCharAt(cursor);
                        redrawLine(line.toString(), cursor);
                    }
                    break;
                }

                case 'H': // Home key (some terminals)
                    cursor = 0;
                    moveCursorToCol(prompter.get().length() + 1);
                    break;

                case 'F': // End key (some terminals)
                    cursor = line.length();
                    moveCursorToCol(prompter.get().length() + cursor + 1);
                    break;

                default:
                    // Unknown sequence – consume remaining bytes until a letter
                    consumeUnknownCsi(csi);
                    break;
            }
            inOut[0] = cursor;
            inOut[1] = historyIndex;
            return cursor;
        }

        // ---- SS3 sequence (ESC O) ----------------------------------------
        if (next == 'O') {
            int ss3;
            try {
                ss3 = input.read();
            } catch (IOException e) {
                return cursor;
            }
            switch (ss3) {
                case 'H': // Home (xterm)
                    cursor = 0;
                    moveCursorToCol(prompter.get().length() + 1);
                    break;
                case 'F': // End (xterm)
                    cursor = line.length();
                    moveCursorToCol(prompter.get().length() + cursor + 1);
                    break;
                default:
                    break;
            }
            inOut[0] = cursor;
            inOut[1] = historyIndex;
            return cursor;
        }

        inOut[0] = cursor;
        inOut[1] = historyIndex;
        return cursor;
    }

    // -----------------------------------------------------------------------
    // Internal – terminal rendering
    // -----------------------------------------------------------------------

    /** Prints the prompt, always starting from column 1. */
    private void printPrompt() {
        output.print(ESC + "1G" + prompter.get());
    }

    /**
     * Redraws the entire current line in-place and repositions the cursor.
     *
     * @param text   current full line text
     * @param cursor desired cursor position (0-based index into {@code text})
     */
    private void redrawLine(String text, int cursor) {
        String prompt = prompter.get();
        // Move to column 1, erase to end of line, reprint prompt + text
        output.print(ESC + "1G" + ESC + "K" + prompt + text);
        // Reposition cursor to correct column
        moveCursorToCol(prompt.length() + cursor + 1);
    }

    /** Moves the cursor to absolute column {@code col} (1-based) on the current row. */
    private void moveCursorToCol(int col) {
        output.print(ESC + col + "G");
    }

    /**
     * Consumes bytes of an unknown CSI sequence until a final byte (0x40–0x7E) is seen.
     *
     * @param firstByte the first byte already read after ESC [
     */
    private void consumeUnknownCsi(int firstByte) {
        if (firstByte >= 0x40 && firstByte <= 0x7E) return; // already final
        try {
            int b;
            do {
                b = input.read();
            } while (b != -1 && (b < 0x40 || b > 0x7E));
        } catch (IOException ignored) {
        }
    }

    // -----------------------------------------------------------------------
    // Internal – word navigation helpers
    // -----------------------------------------------------------------------

    /**
     * Returns the position of the start of the word that is at or before
     * {@code cursor}, suitable for Alt+B / Ctrl+W behaviour.
     */
    private static int wordStartBefore(String s, int cursor) {
        int i = cursor;
        // Skip whitespace immediately before cursor
        while (i > 0 && !Character.isLetterOrDigit(s.charAt(i - 1))) i--;
        // Skip the word characters
        while (i > 0 && Character.isLetterOrDigit(s.charAt(i - 1))) i--;
        return i;
    }

    /**
     * Returns the position just after the end of the word starting at or
     * after {@code cursor}, suitable for Alt+F behaviour.
     */
    private static int wordEndAfter(String s, int cursor) {
        int i = cursor;
        int len = s.length();
        // Skip whitespace at or after cursor
        while (i < len && !Character.isLetterOrDigit(s.charAt(i))) i++;
        // Skip the word characters
        while (i < len && Character.isLetterOrDigit(s.charAt(i))) i++;
        return i;
    }

    // -----------------------------------------------------------------------
    // Internal – history management
    // -----------------------------------------------------------------------

    private void addHistory(String line) {
        // Avoid consecutive duplicates
        if (!history.isEmpty() && history.get(history.size() - 1).equals(line)) return;
        history.add(line);
        if (history.size() > maxHistory) {
            history.remove(0);
        }
    }

    // -----------------------------------------------------------------------
    // Internal – stty helpers
    // -----------------------------------------------------------------------

    /**
     * Saves the current terminal settings by running {@code stty -g} and
     * returning the result as a string.
     *
     * @return saved settings string, or {@code ""} on failure
     */
    private static String saveStty() {
        try {
            Process p = new ProcessBuilder("stty", "-g")
                    .redirectInput(ProcessBuilder.Redirect.INHERIT)
                    .start();
            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            byte[] buf = new byte[256];
            int n;
            java.io.InputStream is = p.getInputStream();
            while ((n = is.read(buf)) != -1) baos.write(buf, 0, n);
            p.waitFor();
            return baos.toString().trim();
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Puts the terminal into raw mode: no echo, no line buffering, no special
     * character processing, character-at-a-time delivery.
     */
    private static void enableRawMode() {
        try {
            new ProcessBuilder("stty", "raw", "-echo", "-icrnl")
                    .redirectInput(ProcessBuilder.Redirect.INHERIT)
                    .start()
                    .waitFor();
        } catch (Exception ignored) {
        }
    }

    /**
     * Restores the terminal to the settings captured by {@link #saveStty()}.
     *
     * @param savedStty the string previously returned by {@link #saveStty()}
     */
    private static void restoreStty(String savedStty) {
        if (savedStty == null || savedStty.isEmpty()) return;
        try {
            new ProcessBuilder("stty", savedStty)
                    .redirectInput(ProcessBuilder.Redirect.INHERIT)
                    .start()
                    .waitFor();
        } catch (Exception ignored) {
        }
    }
}




