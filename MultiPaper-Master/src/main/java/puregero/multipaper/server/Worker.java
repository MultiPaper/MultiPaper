package puregero.multipaper.server;

import java.util.LinkedList;
import java.util.Queue;

public class Worker extends Thread {

    private static final Queue<Runnable> jobs = new LinkedList<>();
    private static int workersRunning = 0;

    public Worker() {
        setName("Worker-" + Thread.currentThread().getId());
        start();
    }

    public static void runAsync(Runnable callback) {
        synchronized (jobs) {
            jobs.add(callback);
            jobs.notify();
        }
    }

    public void run() {
        try {
            while (!isInterrupted()) {
                Runnable callback;

                synchronized (jobs) {
                    while ((callback = jobs.poll()) == null) {
                        jobs.wait();
                    }
                    workersRunning++;
                    // System.out.println(workersRunning + " workers running concurrently");
                }

                try {
                    callback.run();
                } catch (Exception e) {
                    System.err.println("Unhandled exception in " + getName());
                    e.printStackTrace();
                }

                synchronized (jobs) {
                    workersRunning--;
                }
            }
        } catch (InterruptedException e) {
            // Ignored
        }
    }

}
