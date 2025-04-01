package org.gonn.gava;

import java.util.function.*;

/**
 * Static Utils (STU) is a collection of static methods that are frequently used.
 *
 * @author Gon Yi
 * @version 0.1.2
 */
public class Stu {
    public static final boolean VERBOSE_MODE = System.getProperty("VERBOSE", null) != null;
    public static final long EPOCH_STARTED = System.currentTimeMillis();  // this will be used for log

    public static final long SECOND = 1000;
    public static final long MINUTE = SECOND * 60;
    public static final long HOUR = MINUTE * 60;
    public static final long DAY = HOUR * 24;

    public static final String[] EMPTY_STRING_ARRAY = new String[]{};
    public static final char[] EMPTY_CHAR_ARRAY = new char[]{};
    public static final int[] EMPTY_INT_ARRAY = new int[]{};

    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray(); // This is used for hex conversion

    private Stu() {} // Stu is a collection of static methods. Therefore, disable the constructor.

    /**
     * Subset of a T array
     *
     * @param src       Source T array
     * @param start     start index
     * @param end       end index
     * @param generator to create new T[] (ig. String[]::new)
     * @param <T>       array T
     * @return New T array
     */
    public static <T> T[] subset(T[] src, int start, int end, IntFunction<T[]> generator) {
        start = substringCalc(src.length, start, false);
        end = substringCalc(src.length, end, true);
        int size = end - start;
        T[] out = generator.apply(size);
        if (size > 0) System.arraycopy(src, start, out, 0, size);
        return out;
    }

    // ================================================================================
    // Integer
    // ================================================================================

    public static int count(String haystack, char needle) {
        if (haystack == null) return 0;
        int out = 0;
        for (int i = 0; i < haystack.length(); ++i) {
            if (haystack.charAt(i) == needle) out++;
        }
        return out;
    }

    public static int count(String haystack, String needle) {
        if (haystack == null || haystack.isEmpty()
                || needle == null || needle.isEmpty()
                || needle.length() > haystack.length()) return 0;
        int out = 0;
        int cur = 0;
        while ((cur = haystack.indexOf(needle, cur)) > -1) {
            out++;
            cur++;
        }
        return out;
    }

    public static <T> int count(T[] s, T key) {
        int out = 0;
        for (T v : s) {
            if (v != null && v.equals(key)) out++;
        }
        return out;
    }

    public static <T> boolean contain(T[] ts, T key) {
        for (T v : ts) {
            if (v != null && v.equals(key)) return true;
        }
        return false;
    }


    private static int substringCalc(int size, int index, boolean indexTo) {
        int tmp = indexTo ? 1 : 0;
        int p = index < tmp ? (size + index) : index;
        if (p < 0) return 0;
        else if (p > size) return size;
        return p;
    }

    public static String substring(String s, int idxFrom, int idxTo) {
        if (s == null) return null;
        int sz = s.length();
        int p1 = substringCalc(sz, idxFrom, false);
        int p2 = substringCalc(sz, idxTo, true);
        return p1 >= p2 ? "" : s.substring(p1, p2);
    }

    public static String substring(String s, int idx) {
        if (idx == 0) return "";
        if (idx > 0) return substring(s, 0, idx);
        return substring(s, idx, 0);
    }

    public static String getBetween(String s, String prefix, String suffix) {
        if (!isBetween(s, prefix, suffix)) return null;
        return s.substring(prefix.length(), s.length() - suffix.length());
    }

    public static boolean isBetween(String s, String prefix, String suffix) {
        if (s.length() < prefix.length() + suffix.length()) return false;
        return s.startsWith(prefix) && s.endsWith(suffix);
    }

    // ================================================================================
    // Array
    // ================================================================================

    public static String getNth(String s, char delim, int index) {
        if (s == null) return null;
        int n = (index < 0) ? index + count(s, delim) + 1 : index; // reverse
        int end = -1;
        int idx = 0;
        int start;

        for (int i = 0; i < s.length(); ++i) {
            if (s.charAt(i) == delim) {
                start = end + 1;
                end = i;
                if (idx++ == n) return s.substring(start, i);
            }
        }
        return idx == n ? s.substring(end + 1) : null;
    }

    public static <T> T getNth(T[] tArr, int index, T fallback) {
        return tArr.length > index ? tArr[index] : fallback;
    }

    public static <T> T first(T[] t) {
        return (t == null || t.length == 0) ? null : t[0];
    }

    public static <T> T last(T[] t) {
        return (t == null || t.length == 0) ? null : t[t.length - 1];
    }

    public static char first(String s) {
        return (s == null || s.isEmpty()) ? 0 : s.charAt(0);
    }

    public static char last(String s) {
        return (s == null || s.isEmpty()) ? 0 : s.charAt(s.length() - 1);
    }

    public static String removePrefix(String s, String prefix) {
        if (s == null) return null;
        if (prefix == null) return s;
        return s.startsWith(prefix) ? s.substring(prefix.length()) : s;
    }

    public static String removePrefix(String s, char prefix) {
        if (s == null) return null;
        return first(s) == prefix ? s.substring(1) : s;
    }

    public static String removeSuffix(String s, String suffix) {
        if (s == null) return null;
        if (suffix == null) return s;
        return s.endsWith(suffix) ? s.substring(0, s.length() - suffix.length()) : s;
    }

    public static String removeSuffix(String s, char suffix) {
        if (s == null) return null;
        return last(s) == suffix ? s.substring(0, s.length() - 1) : s;
    }

    public static long getEpoch() {
        return System.currentTimeMillis();
    }

    public static String epochToString(final long milliseconds, int offsetHr, final boolean signed) {
        if (offsetHr > 23 || offsetHr < -23) {
            log("epochToString(): invalid offsetHr param: " + offsetHr);
            return "00:00:00.000";
        }

        char[] out = new char[13];
        long epochSmall = ((milliseconds + (offsetHr * HOUR)) % DAY);

        if (milliseconds < 0) {
            out[0] = '-';
            epochSmall = -epochSmall;
        } else {
            out[0] = '+';
        }

        long tmp = (epochSmall % DAY) / HOUR;
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
        out[11] = (char) ('0' + ((tmp / 10) % 10));
        out[12] = (char) ('0' + (tmp % 10));

        if (signed) return new String(out, 0, 13);
        return new String(out, 1, 12);
    }

    public static String epochToString(final long milliseconds, int offsetHr) {
        return epochToString(milliseconds, offsetHr, false);
    }

    public static String epochToString(final long milliseconds) {
        return epochToString(milliseconds, 0, false);
    }

    public static String epochToString() {
        return epochToString(System.currentTimeMillis() - EPOCH_STARTED, 0, false);
    }

    public static void prints(String name, Object... objs) {
        String prefix = name + "[%" + getDigits(objs.length) + "d]: ";
        String fmtString = prefix + "\"%s\"\n";
        String fmtObject = prefix + "<%s>\n";
        String fmtOther = prefix + " %s \n";

        for (int i = 0; i < objs.length; i++) {
            if (objs[i] == null) System.out.printf(fmtOther, i, "null");
            else if (objs[i] instanceof String) System.out.printf(fmtString, i, objs[i]);
            else if (objs[i] instanceof Boolean) System.out.printf(fmtOther, i, objs[i]);
            else if (objs[i] instanceof Number) System.out.printf(fmtOther, i, objs[i]);
            else System.out.printf(fmtObject, i, objs[i]);
        }
    }

    public static void println(Object... any) {
        System.out.print(join(", ", any) + '\n');
    }

    public static String join(String delimiter, Object... objs) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < objs.length; i++) {
            if (i != 0 && delimiter != null) sb.append(delimiter);
            if (objs[i] == null) sb.append("null");
            else if (objs[i] instanceof String) sb.append('"').append(objs[i].toString()).append('"');
            else sb.append(objs[i].toString());
        }
        return sb.toString();
    }

    public static int getDigits(int n) {
        n = n < 0 ? -n : n;
        int out = 1;
        while ((n = n / 10) > 0) out++;
        return out;
    }

    public static int getDigits(long n) {
        n = n < 0 ? -n : n;
        int out = 1;
        while ((n = n / 10) > 0) out++;
        return out;
    }

    public static void parseArgs(String[] args, BiConsumer<String, String> f) {
        for (String a : args) {
            if (a.startsWith("--")) {
                int idx = a.indexOf('=');
                if (idx >= 0) {
                    f.accept(a.substring(2, idx), a.substring(idx + 1));
                } else {
                    f.accept(a.substring(2), null);
                }
            }
        }
    }

    public static StackTraceElement getCaller(int skip) {
        skip += 2;
        StackTraceElement[] tmp = Thread.currentThread().getStackTrace();
        if (tmp.length <= skip) return null;
        return Thread.currentThread().getStackTrace()[skip];
    }

    public static String getCallerString(int skip) {
        StackTraceElement tmp = getCaller(skip);
        if (tmp != null) return tmp.getFileName() + ":" + tmp.getLineNumber();
        return null;
    }

    public static long getTimed(Runnable r) {
        long t = getEpoch();
        r.run();
        return getEpoch() - t;
    }

    public static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            log("Thread.sleep() interrupted: " + e);
            Thread.currentThread().interrupt();
        }
    }

    public static void log(String msg) {
        if (VERBOSE_MODE) System.out.println(epochToString() + "  " + msg);
    }

    public static void log(Supplier<String> msg) {
        if (VERBOSE_MODE) System.out.println(epochToString() + "  " + msg.get());
    }

    public static <T> T mustGet(T t, T fallback) {
        return t != null ? t : fallback;
    }

    public static <X extends Throwable, T> T mustGet(T t, X exception) throws X {
        if (t == null) throw exception;
        return t;
    }

    public static <T> T ifNull(T t, T fallback) {
        return mustGet(t, fallback);
    }

    public static int parseInt(String s) throws NumberFormatException {
        if (s == null) throw new NumberFormatException("null");
        int out = 0;
        char x = '0';
        for (int i = 0; i < s.length(); i++) {
            x = s.charAt(i);
            if (x < '0' || x > '9') throw new NumberFormatException(s);
            out = out * 10 + (x - '0');
        }
        return out;
    }

    public static int parseInt(String s, int fallback) {
        try {
            return parseInt(s);
        } catch (NumberFormatException ignore) {
            return fallback;
        }
    }


    public static long parseLong(String s) throws NumberFormatException {
        if (s == null) throw new NumberFormatException("null");
        long out = 0;
        char x = '0';
        for (int i = 0; i < s.length(); i++) {
            x = s.charAt(i);
            if (x < '0' || x > '9') throw new NumberFormatException(s);
            out = out * 10 + (x - '0');
        }
        return out;
    }

    public static long parseLong(String s, long fallback) {
        try {
            return parseLong(s);
        } catch (NumberFormatException ignore) {
            return fallback;
        }
    }

    public static int getHash(String s) {
        if (s == null) return 0;
        int sLen = s.length();
        int h = 0;
        for (int i = 0; i < sLen; i++) {
            h = h * 31 + s.charAt(i);
        }
        return h;
    }


    public static boolean isDigit(String s) {
        if (s == null || s.isEmpty()) return false;
        char c;
        for (int i = 0; i < s.length(); i++) {
            c = s.charAt(i);
            if (c < '0' || c > '9') return false;
        }
        return true;
    }

    public static boolean isDigit(char c) {
        return '0' <= c && c <= '9';
    }

    public static boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }

    public static boolean isAlphanumeric(String s) {
        if (s == null) return false;
        boolean hasNumber = false;
        boolean hasAlpha = false;
        char c;
        for (int i = 0; i < s.length(); i++) {
            c = s.charAt(i);
            if (isDigit(c)) {hasNumber = true;} else if (isAlpha(c)) {hasAlpha = true;} else {return false;}
        }
        return hasAlpha && hasNumber;
    }

    public static <T> void iterateNotNull(T[] ts, Consumer<T> fx) {
        if (ts == null) return;
        for (T t : ts) if (t != null) fx.accept(t);
    }

    // ================================================================================
    // Bitwise Flag
    // ================================================================================

    public static String bitsToString(long bitflag) {
        return bitsToString(bitflag, -1, 'O', '-', true);
    }

    public static String bitsToString(long bitflag, int size, char on, char off, boolean reverse) {
        if (size < 0) size = 64;
        char[] flagString = new char[size];

        if (reverse) {
            for (int i = 0; i < size; i++)
                flagString[i] = (bitflag & (1 << i)) != 0 ? on : off;
        } else {
            for (int i = 0; i < size; i++)
                flagString[size - i - 1] = (bitflag & (1 << i)) != 0 ? on : off;
        }
        return new String(flagString);
    }

    public static String bitsToString(int bitflag) {
        return bitsToString(bitflag, -1, 'O', '-', true);
    }

    public static String bitsToString(int bitflag, int size, char on, char off, boolean reverse) {
        if (size < 0) size = 32;
        char[] flagString = new char[size];

        if (reverse) {
            for (int i = 0; i < size; i++) {
                flagString[i] = (bitflag & (1 << i)) != 0 ? on : off;
            }
        } else {
            for (int i = 0; i < size; i++) {
                flagString[size - i - 1] = (bitflag & (1 << i)) != 0 ? on : off;
            }
        }
        return new String(flagString);
    }

    public static int setBits(int flag, int mask, boolean to) {
        return to ? (flag | mask) : (flag & ~mask);
    }

    public static boolean hasBits(int flag, int mask, boolean hasAll) {
        return hasAll ? ((flag & mask) == mask) : ((flag & mask) != 0);
    }

    // ================================================================================
    // Chained Functions
    // ================================================================================

    public static <T> T chain(T t, UnaryOperator<T> fx1, UnaryOperator<T> fx2) {
        return fx2.apply(fx1.apply(t));
    }

    public static <T> T chain(T t, UnaryOperator<T> fx1, UnaryOperator<T> fx2, UnaryOperator<T> fx3) {
        return fx3.apply(fx2.apply(fx1.apply(t)));
    }

    public static <T> T chain(T t, UnaryOperator<T> fx1, UnaryOperator<T> fx2, UnaryOperator<T> fx3, UnaryOperator<T> fx4) {
        return fx4.apply(fx3.apply(fx2.apply(fx1.apply(t))));
    }

    // ================================================================================
    // To String
    // ================================================================================


    /**
     * Repeats char c for n times
     *
     * @param c char to repeat
     * @param n number of repeats
     * @return String with repeated char
     */
    public static String repeat(char c, int n) {
        if (n < 1) return "";
        char[] out = new char[n];
        for (int i = 0; i < n; i++) out[i] = c;
        return new String(out);
    }

    /**
     * Repeats a string for n times
     *
     * @param s string to repeat
     * @param n number of repeats
     * @return String with repeated string s
     */
    public static String repeat(String s, int n) {
        if (s == null || n < 1) return "";
        int size = s.length();
        char[] out = new char[size * n];
        for (int i = 0; i < n; i++) s.getChars(0, size, out, i * size);
        return new String(out);
    }

    public static String byteSizeToString(long sizeInByte) {
        final String sign = sizeInByte > 0 ? "" : "-";
        final long size = sizeInByte > 0 ? sizeInByte : -sizeInByte;

        if (size < 1024) return sign + size + "B";

        long unit;
        String unitString;

        if (size < 1048576) {
            unit = 1024;
            unitString = "KB";
        } else if (size < 1073741824) {
            unit = 1048576;
            unitString = "MB";
        } else {
            unit = 1073741824;
            unitString = "GB";
        }

        int num = (int) (size / unit);
        int decimal = (int) ((size % unit) * 100 / unit);
        return sign + num + "." + (decimal / 10) + (decimal % 10) + unitString;
    }

    public static String toHexString(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++) {
            int v = bytes[i] & 0xFF;
            hexChars[i * 2] = HEX_ARRAY[v >>> 4];
            hexChars[i * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static <T> String arrayToString(T[] ts) {
        if (ts == null) return "null";
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        if (ts.length > 0) {
            boolean isString = ts[0] instanceof String;
            for (int i = 0; i < ts.length; i++) {
                if (i != 0) sb.append(", ");
                if (isString) {
                    sb.append('"').append(ts[i]).append('"');
                } else {
                    sb.append(ts[i].toString());
                }
            }
        }
        sb.append(']');
        return sb.toString();
    }

    public static String intComma(long n) {
        if (n == 0) return "0";
        boolean neg = false;
        if (n < 0) {
            n = -n;
            neg = true;
        }

        int length = getDigits(n);
        length = length + (length - 1) / 3 + (neg ? 1 : 0);

        byte[] out = new byte[length];
        if (neg) out[0] = '0';

        int i = length - 1;
        int j = 0;
        while (n > 0) {
            if (j == 3) {
                out[i--] = ',';
                j = 0;
            }
            out[i--] = (byte) ('0' + n % 10);
            n /= 10;
            j++;
        }
        return new String(out);
    }

    public static String intPadding(long positiveNumber, int length, char padding) {
        if (positiveNumber < 0) positiveNumber = -positiveNumber;
        char[] out = new char[length];
        for (int i = 0; i < length; i++) out[i] = padding;
        for (int i = length - 1; i >= 0; i--) {
            if (positiveNumber == 0) break;
            out[i] = (char) ('0' + positiveNumber % 10);
            positiveNumber /= 10;
        }
        return new String(out);
    }

    public static String pad(String s, int min, int max, char padding, boolean leftPadding) {
        if (s == null) return null;
        int sLen = s.length();
        if (sLen >= min) return max > 0 ? s.substring(0, max) : s;

        char[] out = new char[min];
        int i = 0;

        if (leftPadding) {
            for (; i < min - sLen; i++) out[i] = padding;
            for (int j = 0; j < sLen; j++) out[i++] = s.charAt(j);
        } else {
            for (; i < sLen; i++) out[i] = s.charAt(i);
            for (; i < min; i++) out[i] = padding;
        }
        return new String(out);
    }

    public static String padLeft(String s, int length, char padding) {
        return pad(s, length, length, padding, true);
    }

    public static String padRight(String s, int length, char padding) {
        return pad(s, length, length, padding, false);
    }


    public static String trimLeft(String s) {
        if (s == null) return null;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) != ' ') return s.substring(i);
        }
        return "";
    }

    public static String trimRight(String s) {
        if (s == null) return null;
        for (int i = s.length() - 1; i > 0; i--) {
            if (s.charAt(i) != ' ') return s.substring(0, i + 1);
        }
        return "";
    }

    public static String trimString(String input) {
        if (input == null || input.isEmpty()) return input;

        StringBuilder sb = new StringBuilder(input);
        int n = sb.length();
        int index = 0;
        boolean lastWasSpace = true;

        for (int i = 0; i < n; i++) {
            char c = sb.charAt(i);
            if (c != ' ') {
                sb.setCharAt(index++, c);
                lastWasSpace = false;
            } else if (!lastWasSpace) {
                sb.setCharAt(index++, c);
                lastWasSpace = true;
            }
        }

        if (index > 0 && sb.charAt(index - 1) == ' ') index--;

        sb.setLength(index);
        return sb.toString();
    }
}
