package io.multipaper.database.util;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

public class AsyncIO {

    public static final Executor ASYNC_EXECUTOR = r -> Thread.ofVirtual().name("AsyncIO").start(r);

    public static CompletableFuture<Void> runAsync(Runnable runnable) {
        CompletableFuture<Void> future = CompletableFuture.runAsync(runnable, ASYNC_EXECUTOR);
        future.whenComplete((v, t) -> {
            if (t != null) {
                t.printStackTrace();
            }
        });
        return future;
    }

    public static <T> CompletableFuture<T> supplyAsync(Supplier<T> supplier) {
        CompletableFuture<T> future = CompletableFuture.supplyAsync(supplier, ASYNC_EXECUTOR);
        future.whenComplete((v, t) -> {
            if (t != null) {
                t.printStackTrace();
            }
        });
        return future;
    }

}
