package org.gonn.gava;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.function.Consumer;

/**
 * This is only for US ASCII values.
 * This is not thread-safe.
 * <br>
 * Example of a static method FixedRecord.readInputStream():
 * <pre><code>
 *         String[] data = new String[]{
 *                 // 23456789_123456789
 *                 "A0001GON   YI     41",
 *                 "A0002JOHN  DOE    42",
 *                 "A0003PETER PAN III43",
 *         };
 *         try (InputStream in = new ByteArrayInputStream(String.join("", data).getBytes(StandardCharsets.US_ASCII))) {
 *             FixedRecord.readInputStream(in, 20, r -&gt; {
 *                 System.out.printf("ID:    [%s]%n", r.get(0,5));
 *                 System.out.printf("FName: [%s]%n", r.get(5, 6));
 *                 System.out.printf("LName: [%s]%n", r.get(11, 7));
 *                 System.out.printf("Age:   [%s]%n%n", r.get(18, 2));
 *             });
 *         } catch (IOException e) {
 *             // Won't happen with ByteArrayInputStream
 *         }
 * </code></pre>
 */
public class FixedRecord {
    private final byte padByte; // default value
    private final byte[] data;

    public FixedRecord(int size, byte padByte) {
        if (size <= 0) throw new IllegalArgumentException();
        this.data = new byte[size];
        this.padByte = padByte;
        this.reset();
    }

    public FixedRecord(int size) {
        this(size, (byte) ' ');
    }

    public void reset(int start, int end) {
        this.checkRange(start, end - start);
        Arrays.fill(this.data, start, end, this.padByte);
    }

    public void reset() {
        this.reset(0, this.data.length);
    }

    public void set(String s, int start, int length, byte padByte) {
        if (s == null) return;
        this.set(s.getBytes(StandardCharsets.US_ASCII), start, length, padByte);
    }

    public void set(String s, int start, int length) {
        this.set(s, start, length, this.padByte);
    }

    public void set(int start, int length) {
        this.set("", start, length, this.padByte);
    }

    public void set(String line) {
        this.set(line, 0, this.data.length, this.padByte);
    }

    public void set(byte[] s, int start, int length, byte padByte) {
        if (s == null || s.length == 0) return;
        this.checkRange(start, length);

        int sLen = Math.min(s.length, length);
        if (sLen > 0) System.arraycopy(s, 0, this.data, start, sLen);
        if (sLen < length) {
            Arrays.fill(this.data, sLen + start, length + start, padByte);
        }
    }

    public void set(byte[] s, int start, int length) {
        this.set(s, start, length, this.padByte);
    }

    public void set(byte b, int index) {
        if (index < 0 || index >= this.data.length) throw new IllegalArgumentException("Invalid index");
        this.data[index] = b;
    }

    public void set(char c, int index) {
        this.set((byte) c, index);
    }

    public String get(int start, int length) {
        this.checkRange(start, length);
        return new String(this.data, start, length, StandardCharsets.US_ASCII);
    }

    public byte get(int index) {
        if (index < 0 || index >= this.data.length) throw new IllegalArgumentException("Invalid index");
        return this.data[index];
    }

    /**
     * Returns a copy of current underlying byte array
     *
     * @return a copy of current underlying byte array
     */
    public byte[] getBytes() {
        return this.data.clone();
    }

    public boolean copyTo(FixedRecord target) {
        if (target == null || this.data.length != target.data.length) return false;
        System.arraycopy(this.data, 0, target.data, 0, target.data.length);
        return true;
    }

    public boolean copyFrom(FixedRecord source) {
        if (source == null || this.data.length != source.data.length) return false;
        System.arraycopy(source.data, 0, this.data, 0, this.data.length);
        return true;
    }

    @Override
    public String toString() {
        return new String(this.data, StandardCharsets.US_ASCII);
    }

    private void checkRange(int start, int length) {
        if (start < 0 || length < 0 || start + length > this.data.length)
            throw new IllegalArgumentException("Invalid range: start=" + start + ", length=" + length);
    }

    /**
     * Note: partial data will be ignored.
     *
     * @param in          inputStream
     * @param recordWidth width per record
     * @param consumer    how to process the fixed record
     * @param buffer      buffer to use
     * @return total records read
     * @throws IOException any IO issues
     */
    public static int readInputStream(InputStream in, int recordWidth, Consumer<FixedRecord> consumer, byte[] buffer)
            throws IOException {
        if (in == null || consumer == null) throw new IllegalArgumentException("null input");
        if (recordWidth <= 0) throw new IllegalArgumentException("recordWidth must be > 0");
        if (buffer == null) throw new IllegalArgumentException("null buffer");
        if (buffer.length < recordWidth) throw new IllegalArgumentException("buffer must be >= recordWidth");

        FixedRecord record = new FixedRecord(recordWidth);
        int totalRecords = 0;
        int bufferPos = 0;
        int bytesInBuffer = 0;

        while (true) {
            record.reset();
            // Shift remaining partial record to front if needed for refill
            if (bytesInBuffer - bufferPos < recordWidth && bufferPos > 0) {
                int remaining = bytesInBuffer - bufferPos;
                if (remaining > 0) {
                    System.arraycopy(buffer, bufferPos, buffer, 0, remaining);
                }
                bufferPos = 0;
                bytesInBuffer = remaining;
            }

            // Refill buffer if we don't have a full record
            if (bytesInBuffer - bufferPos < recordWidth) {
                int bytesRead = in.read(buffer, bytesInBuffer, buffer.length - bytesInBuffer);
                if (bytesRead == -1) break;  // EOF
                bytesInBuffer += bytesRead;
                if (bytesInBuffer - bufferPos < recordWidth) break;  // EOF with partial record
            }

            // Process full record
            System.arraycopy(buffer, bufferPos, record.data, 0, recordWidth);
            consumer.accept(record);
            bufferPos += recordWidth;
            totalRecords++;
        }
        return totalRecords;
    }

    public static int readInputStream(InputStream in, int recordWidth, Consumer<FixedRecord> consumer)
            throws IOException {
        return readInputStream(in, recordWidth, consumer, new byte[recordWidth]);
    }
}






