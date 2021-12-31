package puregero.multipaper.server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.IntFunction;

public class HeartbeatThread extends Thread {

    public boolean stopped = false;

    public void run() {
        long nextTick = System.currentTimeMillis();

        while (!stopped) {
            // Get all the alive serverConnections
            ServerConnection[] serverConnections;
            synchronized (ServerConnection.getConnections()) {
                serverConnections = ServerConnection.getConnections().stream().filter(ServerConnection::isOnline).toArray(ServerConnection[]::new);
            }

            // Set their onTickFinish
            CompletableFuture<Long>[] onTickFinishes = Arrays.stream(serverConnections).map(serverConnection -> {
                CompletableFuture<Long> onTickFinish = new CompletableFuture<>();
                serverConnection.setOnTickFinish(onTickFinish);
                return onTickFinish;
            }).toArray((IntFunction<CompletableFuture<Long>[]>) CompletableFuture[]::new);

            // Tell all the servers to do their tick, this may help bring a dead server back alive
            try {
                DataOutputStream broadcast = ServerConnection.broadcastAll();
                broadcast.writeInt(-1);
                broadcast.writeUTF("tick");
                broadcast.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Wait for the servers to finish their tick, or timeout after 15 seconds
            try {
                CompletableFuture.allOf(onTickFinishes).get(15, TimeUnit.SECONDS);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                for (int i = 0; i < serverConnections.length; i++) {
                    if (!onTickFinishes[i].isDone()) {
                        System.out.println("Tick timed out waiting for " + serverConnections[i].getBungeeCordName());
                    }
                }
            }

            // Next tick ends in another 50 milliseconds
            nextTick += 50;

            // Check if we're running way behind schedule
            if (System.currentTimeMillis() > nextTick + 10000) {
                System.out.println("Skipping " + (System.currentTimeMillis() - nextTick) / 50 + " ticks (" + (System.currentTimeMillis() - nextTick) + "ms)");
                nextTick = System.currentTimeMillis();
            }

            // Wait for the next tick
            long toWait = Math.max(nextTick - System.currentTimeMillis(), 0);
            try {
                Thread.sleep(toWait);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
