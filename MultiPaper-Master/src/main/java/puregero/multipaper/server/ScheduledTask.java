package puregero.multipaper.server;

public class ScheduledTask implements Runnable {
    private final Runnable runnable;
    private final long time;

    public ScheduledTask(Runnable runnable, long time) {
        this.runnable = runnable;
        this.time = time;
    }

    @Override
    public void run() {
        runnable.run();
    }

    public long getTime() {
        return time;
    }
}
