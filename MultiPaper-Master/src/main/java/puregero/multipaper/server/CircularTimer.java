package puregero.multipaper.server;

public class CircularTimer {
    private long[] times = new long[1200];
    private long total = 0;
    private int index = 0;

    public void append(long time) {
        total -= times[index];
        total += times[index] = time;
        index = (index + 1) % times.length;
    }

    public long averageInMillis() {
        return total / times.length / 1000000;
    }
}
