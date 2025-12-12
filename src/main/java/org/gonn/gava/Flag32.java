package org.gonn.gava;

public class Flag32 {
    private int flags;

    public Flag32() {this.reset();}

    public void set(int flag) {this.flags |= flag;}

    public void unset(int flag) {this.flags &= ~flag;}

    public boolean has(int flag) {return (this.flags & flag) != 0;}

    public void reset() {this.reset(0);}

    public void reset(int flag) {this.flags = flag;}

    public int toInt() {return this.flags;}

    @Override
    public String toString() {
        return Stu.bitsToString(this.flags, 32, 'O', '-', true);
    }

    /**
     * Get the nth flag (starts with 1)
     * @param n the flag index (starting with 1)
     * @return the nth flag
     */
    public static int getNth(int n) {
        return 1 << (n-1);
    }
}
