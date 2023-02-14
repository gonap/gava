/*
 * <https://gonn.org> [++]
 * Copyright (c) 2023 Gon Yi. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 */
package org.gonn.gava;


/**
 * A collection of static methods that are very frequently used.
 *
 * @author Gon Yi
 * @version 1.3.4
 */
public class Common {
    /**
     * Default params set when any of the method was first used.
     * EPOCH_STARTED -- record the time first library was used
     * VERBOSE_MODE  -- whether to log
     */
    public static final String ENV_VAR_VERBOSE = "GAVA_VERBOSE";  // to avoid conflict with other program
    public static final long EPOCH_STARTED = System.currentTimeMillis();
    public static final boolean VERBOSE_MODE = getConfig(ENV_VAR_VERBOSE, false);

    // Various time in milliseconds
    public static final int SECOND = 1000;
    public static final int MINUTE = SECOND * 60;
    public static final int HOUR = MINUTE * 60;
    public static final int DAY = HOUR * 24;

    // Common is a collection of static methods. Therefore, no need for constructor.
    private Common() {
    }

    /**
     * Take a char c and repeat it n times.
     *
     * @param c char to repeat
     * @param n number of repeats
     * @return String of the result
     */
    public static String repeat(char c, int n) {
        if (n < 1) {
            return "";
        }
        char[] out = new char[n];
        for (int i = 0; i < n; i++) {
            out[i] = c;
        }
        return new String(out);
    }

    /**
     * Repeat the input string for n times.
     *
     * @param s A string to be repeated
     * @param n Number of repeats
     * @return Repeated string s
     */
    public static String repeat(String s, int n) {
        if (s == null || n < 1) {
            return "";
        }
        int size = s.length();
        char[] out = new char[size * n];
        for (int i = 0; i < n; i++) {
            s.getChars(0, size, out, i * size);
        }
        return new String(out);
    }

    /**
     * Count char c from String s, returns int of how many times char c was used.
     *
     * @param haystack String s to be checked for char c
     * @param needle   char c to lookup
     * @return number of char c found in String s
     */
    public static int count(String haystack, char needle) {
        if (haystack == null) {
            return 0;
        }
        int out = 0;
        for (int i = 0; i < haystack.length(); ++i) {
            if (haystack.charAt(i) == needle) {
                out++;
            }
        }
        return out;
    }

    /**
     * Count how many times the needle is in haystack.
     *
     * @param haystack A string to be evaluated.
     * @param needle   A string to be found.
     * @return Number of times the string "search" was in s.
     */
    public static int count(String haystack, String needle) {
        if (haystack == null || haystack.length() == 0 || needle == null || needle.length() == 0
                || needle.length() > haystack.length()) {
            return 0;
        }
        int out = 0;
        int cur = 0;
        while ((cur = haystack.indexOf(needle, cur)) > -1) {
            out++;
            cur++;
        }
        return out;
    }

    /**
     * How many times String key was in the String array s.
     *
     * @param s   String array
     * @param key String to search from the array
     * @return number of key in s.
     */
    public static int count(String[] s, String key) {
        int out = 0;
        for (String v : s) {
            if (v != null && v.equals(key)) {
                out++;
            }
        }
        return out;
    }

    /**
     * Right trim the string
     *
     * @param s Input string
     * @return Trimmed string
     */
    public static String rtrim(String s) {
        if (s == null) {
            return null;
        }
        for (int i = s.length() - 1; i > 0; i--) {
            if (s.charAt(i) != ' ') {
                return s.substring(0, i + 1);
            }
        }
        return "";
    }

    /**
     * Left trim the string
     *
     * @param s Input string
     * @return Trimmed string
     */
    public static String ltrim(String s) {
        if (s == null) {
            return null;
        }
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) != ' ') {
                return s.substring(i);
            }
        }
        return "";
    }

    private static int substringCalc(int size, int index, boolean indexTo) {
        int tmp = indexTo ? 1 : 0;
        int p = index < tmp ? (size + index) : index;
        if (p < 0) {
            return 0;
        }
        if (p > size) {
            return size;
        }
        return p;
    }

    /**
     * A static alternative substring method. This method supports negative index.
     * <p><code>
     * String s = "hello Gon";
     * substring(s, -3, 0);   // returns Gon
     * substring(s, -3, -1);  // returns Go
     * </code></p>
     *
     * @param s       input string
     * @param idxFrom index of substring from
     * @param idxTo   index of substring to; when its value is 0, it means until end of the string.
     * @return String value
     */
    public static String substring(String s, int idxFrom, int idxTo) {
        if (s == null) {
            return null;
        }
        int sz = s.length();
        int p1 = substringCalc(sz, idxFrom, false);
        int p2 = substringCalc(sz, idxTo, true);
        return p1 >= p2 ? "" : s.substring(p1, p2);
    }

    /**
     * Return the string until index idx.
     *
     * @param s   Input string
     * @param idx Index of the string. This can be negative number as well.
     * @return Subset of string
     */
    public static String substring(String s, int idx) {
        if (idx == 0) return "";
        if (idx > 0) return substring(s, 0, idx);
        return substring(s, idx, 0);
    }

    /**
     * Get a String between String prefix and String suffix from String s.
     * <p><code>
     * String s = "https://gonn.org/test";
     * getStringBetween(s, "https://", "/test");  // returns gonn.org
     * </code></p>
     *
     * @param s      Input string
     * @param prefix A prefix to search
     * @param suffix A suffix to search
     * @return String value within prefix and suffix. If not matched, returns null.
     */
    public static String getBetween(String s, String prefix, String suffix) {
        if (!isBetween(s, prefix, suffix)) return null;
        return s.substring(prefix.length(), s.length() - suffix.length());
    }

    /**
     * Check if the input has the prefix AND the suffix.
     *
     * @param s      Input string
     * @param prefix A prefix string
     * @param suffix A suffix string
     * @return true if the input has the prefix and suffix.
     */
    public static boolean isBetween(String s, String prefix, String suffix) {
        if (s.length() < prefix.length() + suffix.length()) return false;
        return s.startsWith(prefix) && s.endsWith(suffix);
    }

    /**
     * Returns index element from the String s when separated by char delim.
     *
     * <code>
     * String s = "Gon|Yi|41|Conway|AR|72034";
     * getNth(s, '|', 3); // returns Conway
     * </code>
     *
     * @param s     An input string
     * @param delim A delimiter char to separate
     * @param index An index to search from the separated input.
     * @return Returns the Nth String
     */
    public static String getNth(String s, char delim, int index) {
        if (s == null) {
            return null;
        }
        int n = (index < 0) ? index + count(s, delim) + 1 : index; // reverse
        int end = -1;
        int idx = 0;
        int start;

        for (int i = 0; i < s.length(); ++i) {
            if (s.charAt(i) == delim) {
                start = end + 1;
                end = i;
                if (idx++ == n) {
                    return s.substring(start, i);
                }
            }
        }
        return idx == n ? s.substring(end + 1) : null;
    }

    public static <T> T first(T[] t) {
        return (t == null || t.length == 0) ? null : t[0];
    }

    public static char first(String s) {
        return (s == null || s.length() == 0) ? 0 : s.charAt(0);
    }

    public static <T> T last(T[] t) {
        return (t == null || t.length == 0) ? null : t[t.length - 1];
    }

    public static char last(String s) {
        return (s == null || s.length() == 0) ? 0 : s.charAt(s.length() - 1);
    }

    /**
     * Remove prefix from the String s
     *
     * @param s      Input string
     * @param prefix A prefix string to remove
     * @return A string without the suffix
     */
    public static String removePrefix(String s, String prefix) {
        if (s == null) {
            return null;
        }
        if (prefix == null) {
            return s;
        }
        return s.startsWith(prefix) ? s.substring(prefix.length()) : s;
    }

    /**
     * Remove prefix from the String s
     *
     * @param s      Input string
     * @param prefix A prefix char to remove
     * @return A string without the suffix
     */
    public static String removePrefix(String s, char prefix) {
        if (s == null) {
            return null;
        }
        return first(s) == prefix ? s.substring(1) : s;
    }

    /**
     * Remove the suffix from the String s
     *
     * @param s      Input string
     * @param suffix A suffix string to remove
     * @return A string without the suffix
     */
    public static String removeSuffix(String s, String suffix) {
        if (s == null) {
            return null;
        }
        if (suffix == null) {
            return s;
        }
        return s.endsWith(suffix) ? s.substring(0, s.length() - suffix.length()) : s;
    }

    /**
     * Remove the suffix from the String s
     *
     * @param s      Input string
     * @param suffix A suffix char to remove
     * @return A string without the suffix
     */
    public static String removeSuffix(String s, char suffix) {
        if (s == null) {
            return null;
        }
        return last(s) == suffix ? s.substring(0, s.length() - 1) : s;
    }

    /**
     * Returns current epoch milliseconds.
     *
     * @return epochMillis
     */
    public static long getEpoch() {
        return System.currentTimeMillis();
    }

    /**
     * Converts epoch milliseconds to human-readable time format.
     * Note: Date is not considered here, and returns only time part (hour, minute, second, and millisecond)
     *
     * @param epoch    Epoch at milliseconds level
     * @param offsetHr Any offset in hour. (i.g. -6 for CST) If 0 is given, this will return UTC.
     * @param signed   Whether to show +/- sign.
     * @return Human-readable epoch time. i.g. "15:04:05.000"
     */
    public static String epochToString(final long epoch, int offsetHr, final boolean signed) {
        if (offsetHr > 23 || offsetHr < -23) {
            error(() -> "getEpochString(): invalid param(s)");
            return "00:00:00.000";
        }

        char[] out = new char[13]; // 13 bytes total

        int epochSmall = (int) ((epoch + (offsetHr * HOUR)) % DAY); // intMax: 2,147,483,647

        out[0] = '+';
        if (epoch < 0) {
            out[0] = '-';
            epochSmall = -epochSmall;
        }

        int tmp = (epochSmall % DAY) / HOUR;
        out[1] = (char) ('0' + tmp / 10);
        out[2] = (char) ('0' + tmp % 10);
        out[3] = ':';
        tmp = (epochSmall % HOUR) / MINUTE;
        out[4] = (char) ('0' + tmp / 10);
        out[5] = (char) ('0' + tmp % 10);
        out[6] = ':';
        tmp = (epochSmall % MINUTE) / SECOND;
        out[7] = (char) ('0' + tmp / 10);
        out[8] = (char) ('0' + tmp % 10);
        out[9] = '.';
        tmp = (epochSmall % SECOND);
        out[10] = (char) ('0' + (tmp / 100));
        out[11] = (char) ('0' + (tmp / 10) % 10);
        out[12] = (char) ('0' + (tmp % 10));

        if (signed) {
            return new String(out, 0, 13);
        }
        return new String(out, 1, 12);
    }

    /**
     * Return epoch time with offset.
     *
     * @param epoch    epoch time
     * @param offsetHr UTC offset in hour (e.g. -6 for CST)
     * @return String value
     */
    public static String epochToString(final long epoch, int offsetHr) {
        return epochToString(epoch, offsetHr, false);
    }

    /**
     * Returns UTC time from epochMillis.
     *
     * @param epochMillis epoch millisecond
     * @return String format such as 15:04:05.000
     */
    public static String epochToString(long epochMillis) {
        return epochToString(epochMillis, 0, epochMillis < 0);
    }

    /**
     * Time since EPOCH_STARTED in string format
     *
     * @return formatted epoch since
     */
    public static String epochToString() {
        return epochToString(System.currentTimeMillis() - EPOCH_STARTED);
    }

    /**
     * Prints any objects in a line per item.
     * This can be used during quick debugging.
     *
     * @param name Name of the group
     * @param objs Any object(s)
     */
    public static void prints(String name, Object... objs) {
        String prefix = name + "[%" + getDigits(objs.length) + "d]: ";
        String fmtString = prefix + "\"%s\"\n";
        String fmtObject = prefix + "<%s>\n";
        String fmtOther = prefix + " %s \n";

        for (int i = 0; i < objs.length; i++) {
            if (objs[i] == null)
                System.out.printf(fmtOther, i, "null");
            else if (objs[i] instanceof String)
                System.out.printf(fmtString, i, objs[i]);
            else if (objs[i] instanceof Boolean) // color true/false
                System.out.printf(fmtOther, i, objs[i]);
            else if (objs[i] instanceof Number)
                System.out.printf(fmtOther, i, objs[i]);
            else {
                System.out.printf(fmtObject, i, objs[i]);
            }
        }
    }

    public static void println(Object... any) {
        System.out.print(join(", ", any) + '\n');
    }

    public static String join(String delimiter, Object... objs) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < objs.length; i++) {
            if (i != 0 && delimiter != null) {
                sb.append(delimiter);
            }
            if (objs[i] == null) {
                sb.append("null");
            } else if (objs[i] instanceof String) {
                sb.append('"').append(objs[i].toString()).append('"');
            } else {
                sb.append(objs[i].toString());
            }
        }
        return sb.toString();
    }


    /**
     * Get an integer and calculate number of digits.
     * Note that this uses long type even when int or short type is given.
     *
     * @param n A number to evaluate
     * @return Number of digits in n.
     */
    public static int getDigits(long n) {
        int out = n < 0 ? 2 : 1;
        long num = n < 0 ? -n : n;
        while ((num = num / 10) > 0) {
            out++;
        }
        return out;
    }

    public static int getDigits(int n) {
        return getDigits((long) n);
    }


    // ======================================================================
    // SIZE
    // ======================================================================

    /**
     * Get bytes in long, and returns human-readable format with 2 digit decimal.
     * If the size is negative, it will have "-" (minus sign).
     * This is the simplest and only handles from B to GB. (e.g. 1TB will be 1024GB)
     *
     * @param sizeInByte Size of a file or memory in bytes (B).
     * @return Size in string with the unit such as "12.00KB" with two decimals points.
     */
    public static String byteSizeToString(long sizeInByte) {
        final String sign = sizeInByte > 0 ? "" : "-";
        final long size = sizeInByte > 0 ? sizeInByte : -sizeInByte;

        // BYTES
        if (size < 1024) {
            return sign + size + "B";
        }

        long unit;
        String unitString;

        // KB, MB, GB
        if (size >= 1073741824) {
            unit = 1073741824;
            unitString = "GB";
        } else if (size >= 1048576) {
            unit = 1048576;
            unitString = "MB";
        } else {
            unit = 1024;
            unitString = "KB";
        }

        int num = (int) (size / unit); // whole number
        int decimal = (int) ((size % unit) * 100 / unit); // decimal

        return sign + num + "." + (decimal / 10) + (decimal % 10) + unitString;
    }


    // ======================================================================
    // ENV
    // ======================================================================

    /**
     * Check (1) system properties, and then (2) environmental variables.
     * IF "false" is to be noticed, set fallback as "true."
     * IF "true" is to be noticed, set fallback as "false."
     *
     * @param key      property/variable name
     * @param fallback default fallback value
     * @return boolean value
     */
    public static boolean getConfig(String key, boolean fallback) {
        String res = getConfig(key);
        if (res == null) {
            return fallback;
        } // if not present, return a fallback (default)
        res = res.toUpperCase();
        return fallback ? (!res.equals("FALSE")) : res.equals("TRUE");
    }

    public static String getConfig(String key, String fallback) {
        String res = getConfig(key);
        return res != null ? res : fallback;
    }

    public static String getConfig(String key) {
        String tmp = System.getProperty(key);
        if (tmp != null) {
            return tmp;
        }
        tmp = System.getenv(key);
        return tmp;
    }

    /**
     * Handles args such as `--name="Gon"`
     *
     * @param args String[] as main args
     * @param f    BiConsumer function, when single param such as `--disable` is given, 2nd argument for the BiConsumer
     *             will be null.
     */
    public static void parseArgs(String[] args, Fx20<String, String> f) {
        for (String a : args) {
            if (a.startsWith("--")) {
                int idx = a.indexOf('=');
                if (idx >= 0) { // beginning index 2 because of the prefix "--"
                    f.run(a.substring(2, idx), a.substring(idx + 1));
                } else {
                    f.run(a.substring(2), null);
                }
            }
        }
    }

    /**
     * Get a StackTraceElement of the caller to find out who called this function.
     *
     * @param skip how many skips required
     * @return Caller's StackTraceElement.
     */
    public static StackTraceElement getCaller(int skip) {
        // getCaller(0): whoever called getCaller() method.
        // getCaller(1): whoever called a method that called getCaller() method. (1 level deeper)
        // returns null if n is outside the range
        skip += 2; // ignore Thread.getStackTrace and GavaTest.getCaller
        StackTraceElement[] tmp = Thread.currentThread().getStackTrace();
        if (tmp.length <= skip)
            return null;
        return Thread.currentThread().getStackTrace()[skip];
    }

    /**
     * Returns caller information in "filename:lineNumber" format. (e.g. "Gava.java:123")
     *
     * @return String value file-line
     */
    public static String getCaller() {
        StackTraceElement tmp = getCaller(1);
        if (tmp != null)
            return tmp.getFileName() + ':' + tmp.getLineNumber();
        return null;
    }

    /**
     * Run lambda function Runnable r for count times.
     *
     * @param r     lambda to run
     * @param count how many times to run
     * @return duration of the run in milliseconds.
     */
    public static long getTimed(Runnable r, int count) {
        long t = getEpoch();
        for (int i = 0; i < count; i++) {
            r.run();
        }
        return getEpoch() - t;
    }

    /**
     * Shortcut function that calls Thread.sleep() but does not require try/catch.
     * This returns true, when sleep ended normally, otherwise false.
     * Note that this method does not throw any exception, but will log if verbose mode.
     *
     * @param ms milliseconds
     * @return true if okay, otherwise false.
     */
    public static boolean sleep(long ms) {
        try {
            Thread.sleep(ms);
            return true;
        } catch (InterruptedException e) {
            error(e::toString);
            Thread.currentThread().interrupt();
            return false;
        }
    }


    // ======================================================================
    // LOGGER
    // ======================================================================

    /**
     * Whenever static method log() is called, it will print to System.out.
     * To control, one can use a static method isVerbose() to check if the mode is verbose
     * <code>
     * if(isVerbose()) log(" [DEBUG] ", "debug!");  // Usage 1 -- completely manually, not recommended.
     * if(isVerbose()) info("some info here");      // Usage 2 -- recommended when the message isn't static.
     * </code>
     *
     * @param prefix to be added right after the timestamp.
     * @param msg    message to print.
     */
    public static void log(String prefix, String msg) {
        // use print instead of println as println calls two synchronized blocks.
        System.out.print(
                epochToString(System.currentTimeMillis() - EPOCH_STARTED)
                        + prefix + msg
                        + (last(msg) != '\n' ? '\n' : ' ') // if not ends with newline, add newline
        );
    }

    public static void debug(Fx01<String> s) {
        if (VERBOSE_MODE) log(" DEBUG  ", s.run());
    }

    public static void info(Fx01<String> s) {
        if (VERBOSE_MODE) log(" INFO  ", s.run());
    }

    public static void warn(Fx01<String> s) {
        if (VERBOSE_MODE) log(" WARN  ", s.run());
    }

    public static void error(Fx01<String> s) {
        if (VERBOSE_MODE) log(" ERROR  ", s.run());
    }


    // ======================================================================
    // OTHER
    // ======================================================================

    /**
     * Evaluate the function without a param
     * (This is just to reduce code.)
     *
     * @param evalFn A lambda function returns R
     * @param <R>    Output type
     * @return Result from evalFn
     */
    public static <R> R eval(Fx01<R> evalFn) {
        return evalFn.run();
    }

    /**
     * Evaluate the function with a param.
     * (This is just to reduce code.)
     *
     * @param t      An input value to be evaluated
     * @param evalFn A lambda function takes T and returns R.
     * @param <T>    Input type
     * @param <R>    Output type
     * @return Result from evalFn
     */
    public static <T, R> R eval(T t, Fx11<T, R> evalFn) {
        return evalFn.run(t);
    }

    /**
     * Evaluate the function with a param.
     * (This is just to reduce code.)
     *
     * @param t1     First param to the lambda
     * @param t2     Second param to the lambda
     * @param evalFn A lambda eval function
     * @param <T1>   Type of first param
     * @param <T2>   Type of second param
     * @param <R>    Type of the result.
     * @return Result from the lambda
     */
    public static <T1, T2, R> R eval(T1 t1, T2 t2, Fx21<T1, T2, R> evalFn) {
        return evalFn.run(t1, t2);
    }

    /**
     * If t is null, return fallback value.
     *
     * @param t value to exam
     * @param fallback value to return if t is null
     * @param <T> output type
     * @return t if t is not null otherwise fallback.
     */
    public static <T> T mustGet(T t, T fallback) {
        return t != null ? t : fallback;
    }

    /**
     * If t is null, throw an exception
     *
     * @param t value to exam
     * @param exception exception to throw if t is null
     * @param <X> throwable type'
     * @param <T> input type
     * @return t if t is not null
     * @throws X if t is null
     */
    public static <X extends Throwable, T> T mustGet(T t, X exception) throws X {
        if(t == null) throw exception;
        return t;
    }

    /**
     * Get hash from the input string
     *
     * @param s Input string
     * @return Hashed integer
     */
    public static int getHash(String s) {
        if (s == null) return 0;

        int sLen = s.length();
        int h = 0;
        for (int i = 0; i < sLen; i++) {
            h = h * 31 + s.charAt(i);
        }
        return h;
    }

    // ======================================================================
    // MAIN for testing
    // ======================================================================
    public static void main(String[] args) {
        System.out.println(Common.class.getPackage().getName() + " by Gonn <https://gonn.org>");
    }
}


