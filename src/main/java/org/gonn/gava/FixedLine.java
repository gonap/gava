package org.gonn.gava;

public class FixedLine {
	private final byte filler;
	private final byte[] line;

	public FixedLine(int size, byte filler) {
		this.line = new byte[size];
		this.filler = filler;
		this.reset();
	}

	public FixedLine(int size) {
		this(size, (byte) ' ');
	}

	public void reset(int start, int end) {
		if (this.line.length < end) end = this.line.length;
		for (int i = start; i < end; i++) { this.line[i] = this.filler; }
	}

	public void reset() {
		this.reset(0, this.line.length);
	}

	public void set(String s, int start, int length, byte filler) {
		if (s == null) s = "";
		int sLen = s.length();
		if (length > this.line.length) { length = this.line.length; }
		if (sLen > length) { sLen = length; }
		for (int i = 0; i < sLen; i++) { this.line[i + start] = (byte) s.charAt(i); }
		if (sLen < length) {
			for (int i = 0; i < length; i++) { this.line[i + start] = filler; }
		}
	}

	public void set(String s, int start, int length) {
		this.set(s, start, length, this.filler);
	}

	public void set(int start, int length) {
		this.set("", start, length, this.filler);
	}

	public void set(String line) {
		this.set(line, 0, this.line.length, this.filler);
	}

	public void set(byte[] s, int start, int length, byte filler) {
		int sLen = s.length;
		if (sLen > length) { sLen = length; }
		if (sLen > 0) System.arraycopy(s, 0, this.line, start, sLen);
		if (sLen < length) {
			for (int i = sLen; i < length; i++) { this.line[i + start] = filler; }
		}
	}

	public void set(byte[] s, int start, int length) {
		this.set(s, start, length, this.filler);
	}

	public void set(byte b, int index) { this.line[index] = b; }

	public void set(char c, int index) { this.line[index] = (byte) c; }

	public String get(int start, int length) {
		if (length == -1) { length = this.line.length - start; }
		return new String(this.line, start, length);
	}

	public char get(int start) {
		return (char) this.line[start];
	}

	public String toString() { return new String(this.line); }

	public byte[] getBytes() { return this.line; }

	public boolean copyTo(FixedLine dst) {
		if (dst == null || this.line.length != dst.line.length) return false;
		System.arraycopy(this.line, 0, dst.line, 0, dst.line.length);
		return true;
	}

	public boolean copyFrom(FixedLine src) {
		if (src == null || this.line.length != src.line.length) return false;
		System.arraycopy(src.line, 0, this.line, 0, this.line.length);
		return true;
	}
}






