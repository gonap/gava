/*
 * <https://gonn.org> [++]
 * Copyright (c) 2023 Gon Yi. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 */
package org.gonn.gava;

/**
 * DevLogger is a temporary simplest logger to be used during early stage of development.
 * To activate, set property or env variable for "LOG_LEVEL_MYAPP" to "DEBUG", and "LOG_OUTPUT" to "stdout"
 * TO activate all, set property "LOG_LEVEL" to "DEBUG", "LOG_OUTPUT" to "stdout"
 * NOTE: setting output is available only at global level.
 *
 * @author Gon Yi
 * @version 1.3.1
 */
public class TempLogger implements Loggable<String> {
    public static final byte LV_ALL = 0;
    public static final byte LV_TRACE = 1;
    public static final byte LV_DEBUG = 2;
    public static final byte LV_INFO = 3;
    public static final byte LV_WARN = 4;
    public static final byte LV_ERROR = 5;
    public static final byte LV_FATAL = 6;
    public static final byte LV_OFF = 9;
    public static final String ENV_LOG_OUTPUT = "LOG_OUTPUT";
    public static final String ENV_LOG_LEVEL = "LOG_LEVEL";
    public static final String ENV_LOG_TIMESTAMP = "LOG_TIMESTAMP";
    public static final String ENV_LOG_FILELINE = "LOG_FILELINE";
    private static final String ME = TempLogger.class.getSimpleName();  // this is a class name in string.
    private final String name;
    private Fx20<Byte, String> writer;

    // Default value
    private byte level = LV_OFF;
    private boolean enabled = false;
    private boolean useTimestamp = true;
    private boolean useFileLine = false;

    /**
     * When DevLogger is created, it will get Property and ENV variables for activating the logger.
     * Since this is only meant for small project where large logger is not expected, default
     * behavior is inactivated. If the class name is MyApp, `MYAPP_LOG_LEVEL` will be used to
     * activate the logger.
     * - LOG_OUTPUT=STDOUT
     * - LOG_LEVEL=INFO
     * - LOG_TIMESTAMP=true
     * - LOG_FILELINE=true
     * - MYAPP_LOG_LEVEL=DEBUG
     *
     * @param name of the class the logger belongs to.
     */
    public TempLogger(String name) {
        this.name = name; // set object's name

        // LOG LEVEL: Use individual object's level first, IF NOT, use global.
        // IF logger name is set to "MyApp", MYAPP_LOG_OUTPUT will be checked first,
        // and then LOG_OUTPUT.
        String tmpThisLevel = this.name.toUpperCase() + "_" + TempLogger.ENV_LOG_LEVEL;
        String tmpLevel = Stu.getConfig(tmpThisLevel, null);
        tmpLevel = (tmpLevel != null) ? tmpLevel : Stu.getConfig(TempLogger.ENV_LOG_LEVEL, "");
        this.setLevel(tmpLevel);


        // LOG OUTPUT: Output are the global level.
        String tmpOut = Stu.getConfig(ENV_LOG_OUTPUT, "").toUpperCase();
        if (tmpOut.length() == 0 || tmpOut.equals("STDOUT")) {
            this.setOutput((lv, s) -> System.out.println(s));  // default to stdout
        } else if (tmpOut.equals("STDERR")) {
            this.setOutput((lv, s) -> System.err.println(s));
        }

        this.useTimestamp = Stu.getConfig(ENV_LOG_TIMESTAMP, true);
        this.useFileLine = Stu.getConfig(ENV_LOG_FILELINE, false);
        this.enable(true); // enable the logger based on level and output
    }

    public TempLogger(Class<?> c) {
        this(c.getSimpleName());
    }

    /**
     * Logger name to have optional identifier such as filename.
     * <code>Logger(SomeProcess.class, "abc.txt");</code>
     *
     * @param c          any class
     * @param identifier if there's additional information about the logger
     */
    public TempLogger(Class<?> c, String identifier) {
        this(c.getSimpleName() + ":" + identifier);
    }

    private static String formatter(String name, String level, Fx01<String> msg, boolean useTimestamp, boolean useFileLine, int skip) {
        StringBuilder sb = new StringBuilder(200);
        // Add timestamp
        if (useTimestamp) {
            sb.append(Stu.epochToString()).append("  ");
        }
        // Add level, name, and message
        sb.append(level)
                .append("  [").append(name).append("]  ")
                .append(msg != null ? msg.run() : "null");
        // Add fileline
        if (useFileLine) {
            StackTraceElement caller = Stu.getCaller(2 + skip);
            if (caller != null) {
                return sb.append("  (")
                        .append(caller.getFileName())
                        .append(':').append(caller.getLineNumber()).append(')').toString();
            }
        }
        // IF (useDetail is false) OR (caller is null)
        return sb.toString();
    }

    private static byte parseLevel(char c) {
        // Possible first char: A, T, D, I, W, E, F, N
        if ('a' <= c && c <= 'z') c -= 'a' - 'A';  // make tmp to uppercase

        switch (c) {
            case 'A':
                return LV_ALL;
            case 'T':
                return LV_TRACE;
            case 'D':
                return LV_DEBUG;
            case 'I':
                return LV_INFO;
            case 'W':
                return LV_WARN;
            case 'E':
                return LV_ERROR;
            case 'F':
                return LV_FATAL;
            default:
                return LV_OFF;
        }
    }

    /**
     * Set output writer function
     *
     * @param writer consumer function takes a string.
     * @return self
     */
    public TempLogger setOutput(Fx20<Byte, String> writer) {
        this.writer = writer;
        return this.enable(true);
    }

    // To check if output and log level is set.
    private boolean isLoggable() {
        return (this.writer != null) && (this.level < LV_OFF);
    }

    // Enables the logger IF tf is true, and output is set.
    // This only enables when it can be enabled.
    private TempLogger enable(boolean tf) {
        this.enabled = tf && isLoggable();
        return this;
    }

    /**
     * Set output format to include timestamp and fileline.
     *
     * @param timestamp from the starting of (DevLogger) code. (true/false)
     * @param fileline  filename and line number. (true/false)
     * @return self
     */
    public TempLogger setFormat(boolean timestamp, boolean fileline) {
        this.useTimestamp = timestamp;
        this.useFileLine = fileline;
        return this;
    }

    /**
     * Set level, either take full string, first character, OR the static variables starting with "LV_".
     *
     * @param level String, char, integer, or byte
     * @return self
     */
    public TempLogger setLevel(String level) {
        if (level == null || level.length() == 0) return this;
        return this.setLevel(parseLevel(level.charAt(0)));
    }

    public TempLogger setLevel(byte level) {
        this.level = level;
        return this.enable(true);
    }

    public TempLogger setLevel(int level) {
        return this.setLevel((byte) level);
    }

    public TempLogger setLevel(char level) {
        return this.setLevel(parseLevel(level));
    }


    @Override
    public void trace(Fx01<String> msg) {
        if (this.enabled && this.level <= TempLogger.LV_TRACE)
            this.writer.run(TempLogger.LV_TRACE,
                    TempLogger.formatter(this.name, "TRACE", msg, this.useTimestamp, this.useFileLine, 0));
    }

    @Override
    public void debug(Fx01<String> msg) {
        if (this.enabled && this.level <= TempLogger.LV_DEBUG)
            this.writer.run(TempLogger.LV_DEBUG,
                    TempLogger.formatter(this.name, "DEBUG", msg, this.useTimestamp, this.useFileLine, 0));
    }

    @Override
    public void info(Fx01<String> msg) {
        if (this.enabled && this.level <= TempLogger.LV_INFO)
            this.writer.run(TempLogger.LV_INFO,
                    TempLogger.formatter(this.name, "INFO ", msg, this.useTimestamp, this.useFileLine, 0));
    }

    @Override
    public void warn(Fx01<String> msg) {
        if (this.enabled && this.level <= TempLogger.LV_WARN)
            this.writer.run(TempLogger.LV_WARN,
                    TempLogger.formatter(this.name, "WARN ", msg, this.useTimestamp, this.useFileLine, 0));
    }

    @Override
    public void error(Fx01<String> msg) {
        if (this.enabled && this.level <= LV_ERROR)
            this.writer.run(LV_ERROR,
                    TempLogger.formatter(this.name, "ERROR", msg, this.useTimestamp, this.useFileLine, 0));
    }

    @Override
    public void fatal(Fx01<String> msg) {
        if (this.enabled && this.level <= TempLogger.LV_FATAL)
            this.writer.run(TempLogger.LV_FATAL,
                    TempLogger.formatter(this.name, "FATAL", msg, this.useTimestamp, this.useFileLine, 0));
    }

    public void warn(Fx01<String> msg, int skip) {
        if (this.enabled && this.level <= TempLogger.LV_WARN)
            this.writer.run(TempLogger.LV_WARN,
                    TempLogger.formatter(this.name, "WARN ", msg, this.useTimestamp, this.useFileLine, skip));
    }

    public void error(Fx01<String> msg, int skip) {
        if (this.enabled && this.level <= TempLogger.LV_ERROR)
            this.writer.run(TempLogger.LV_ERROR,
                    TempLogger.formatter(this.name, "ERROR", msg, this.useTimestamp, this.useFileLine, skip));
    }

    public void fatal(Fx01<String> msg, int skip) {
        if (this.enabled && this.level <= TempLogger.LV_FATAL)
            this.writer.run(TempLogger.LV_FATAL,
                    TempLogger.formatter(this.name, "FATAL", msg, this.useTimestamp, this.useFileLine, skip));
    }

    @Override
    public String toString() {
        return ME + "<" + this.name + ">";
    }

    /**
     * Enables all options. A code calling this to be removed when goes to production.
     *
     * @return GavaLog for chaining...
     */
    public TempLogger testing() {
        this.setOutput((lv, s) -> System.err.println(s))
                .setFormat(true, true)
                .setLevel(LV_ALL)
                .warn(() -> "TESTING IS ON - Disable testing() before deployment", 1);
        return this;
    }
}
