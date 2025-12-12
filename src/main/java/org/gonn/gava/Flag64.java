package org.gonn.gava;

public class Flag64 {
    private long flags;

    public Flag64() {this.reset();}

    public void set(long flag) {this.flags |= flag;}

    public void unset(long flag) {this.flags &= ~flag;}

    public boolean has(long flag) {return (this.flags & flag) != 0;}

    public void reset() {this.reset(0);}

    public void reset(long flag) {this.flags = flag;}

    public long toLong() {return this.flags;}

    @Override
    public String toString() {
        return Stu.bitsToString(this.flags, 64, 'O', '-', true);
    }

    /**
     * Get the nth flag (starts with 1)
     * @param n the flag index (starting with 1)
     * @return the nth flag
     */
    public static long getNth(int n) {
        return 1L << (n-1);
    }
}
