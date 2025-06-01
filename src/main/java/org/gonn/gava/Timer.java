package org.gonn.gava;

public class Timer {
    private long t = 0;

    public Timer() {this.reset();}

    public Timer reset() {
        this.t = 0;
        return this;
    }

    public boolean isRunning() {return this.t > 0;}

    public void start() {
        if (this.isRunning()) return;
        this.t = System.currentTimeMillis() + this.t;
    }

    public void stop() {
        long tmp = System.currentTimeMillis();
        if (this.isRunning()) this.t = -(tmp - t);
    }

    public long getElapsed() {
        long tmp = System.currentTimeMillis();
        if (this.isRunning()) return tmp - t;
        return -this.t;
    }
}
