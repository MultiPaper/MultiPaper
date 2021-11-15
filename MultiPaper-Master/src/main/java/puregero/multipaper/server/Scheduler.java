package puregero.multipaper.server;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class Scheduler extends Thread {
    private static final List<ScheduledTask> tasks = new ArrayList<>();
    private static Scheduler scheduler = null;

    public static void schedule(Runnable runnable, int millis) {
        synchronized (tasks) {
            tasks.add(new ScheduledTask(runnable, System.currentTimeMillis() + millis));
            tasks.notify();

            if (scheduler == null) {
                scheduler = new Scheduler();
            }
        }
    }

    private Scheduler() {
        start();
    }

    @Override
    public void run() {
        long nextRun;

        while (true) {
            nextRun = Long.MAX_VALUE;

            synchronized (tasks) {
                Iterator<ScheduledTask> iterator = tasks.iterator();
                while (iterator.hasNext()) {
                    ScheduledTask task = iterator.next();

                    if (task.getTime() < System.currentTimeMillis()) {
                        CompletableFuture.runAsync(task);
                        iterator.remove();
                    } else if (task.getTime() < nextRun) {
                        nextRun = task.getTime();
                    }
                }

                long wait = nextRun - System.currentTimeMillis();
                if (wait > 0) {
                    try {
                        tasks.wait(wait);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        return;
                    }
                }
            }
        }
    }
}
