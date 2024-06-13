package io.multipaper.database;

public class CircularTimer {
    private final long[] times = new long[1200];
    private long total = 0;
    private int index = 0;

    public synchronized void append(long time) {
        this.total -= this.times[this.index];
        this.total += this.times[this.index] = time;
        this.index = (this.index + 1) % this.times.length;
    }

    public synchronized long averageInMillis() {
        return this.total / this.times.length / 1000000;
    }
}
